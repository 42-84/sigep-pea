-- =====================================================================================
-- SIGEP v2 - Creation du schema SIGEP_APP
-- Version applicative : 2.0.0 (mise en production 14/03/2016)
-- Auteur : M. Aubert (DBA) / J. Martin (DSI) - 22/02/2016
-- Cible  : Oracle 11g R2 - instance SIGEPREC (recette) puis SIGEPPRD (prod)
-- Execution : sqlplus / as sysdba @2016-03_V2.0.0_creation_schema.sql
--
-- Le prefixe SGP_ et les colonnes d'audit (DT_CREATION, DT_MAJ, USR_MAJ) sont repris du
-- modele SIGEP v1 (2004) pour faciliter la reprise. Les sequences demarrent a 1 ; la reprise
-- de donnees les repositionne au-dela des identifiants repris.
--
-- Types : identifiants NUMBER(19) ; montants EUR NUMBER(15,2) ; cours et PRU NUMBER(15,4) ;
-- quantites NUMBER(18,3) ; booleens NUMBER(1) (0/1) ; dates sans heure DATE ; horodatages
-- TIMESTAMP. Les enumerations Java sont stockees en texte (VARCHAR2) SAUF SGP_PLAN.STATUT,
-- stocke en ordinal NUMBER(1) (0 OUVERT, 1 CLOTURE, 2 TRANSFERE) - choix de la reprise v1.
-- =====================================================================================

SET ECHO ON
SET DEFINE OFF
WHENEVER SQLERROR EXIT SQL.SQLCODE

-- -------------------------------------------------------------------------------------
-- 0. Compte applicatif et privileges
--    SIGEP_APP est proprietaire des objets. Les privileges CREATE TABLE / CREATE SEQUENCE
--    sont necessaires a Hibernate (ddl-auto=update) en plus des scripts DBA.
-- -------------------------------------------------------------------------------------
CREATE TABLESPACE SIGEP_DATA DATAFILE '+DATA' SIZE 2G AUTOEXTEND ON NEXT 256M MAXSIZE 20G;
CREATE TABLESPACE SIGEP_IDX  DATAFILE '+DATA' SIZE 1G AUTOEXTEND ON NEXT 128M MAXSIZE 10G;

CREATE USER SIGEP_APP IDENTIFIED BY "Sigep#Prd2016"
  DEFAULT TABLESPACE SIGEP_DATA
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON SIGEP_DATA
  QUOTA UNLIMITED ON SIGEP_IDX;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW, CREATE SYNONYM TO SIGEP_APP;
GRANT SELECT ON V_$SESSION TO SIGEP_APP;   -- pool Tomcat : validation-query et diagnostic

-- compte de lecture pour le decisionnel et les extractions BO (mot de passe fourni a part)
CREATE USER SIGEP_LECTURE IDENTIFIED BY "SigepLect#2016" DEFAULT TABLESPACE SIGEP_DATA;
GRANT CREATE SESSION TO SIGEP_LECTURE;

ALTER SESSION SET CURRENT_SCHEMA = SIGEP_APP;

-- -------------------------------------------------------------------------------------
-- 1. Sequences (INCREMENT BY 1, allocationSize = 1 cote JPA)
-- -------------------------------------------------------------------------------------
CREATE SEQUENCE SEQ_SGP_HABILITATION       START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_CLIENT             START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_VALEUR             START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_COURS              START WITH 1 INCREMENT BY 1 CACHE 20 NOCYCLE;
CREATE SEQUENCE SEQ_SGP_COUPON             START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_COMPTE_ESPECES     START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_PLAN               START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_OPERATION_ESPECES  START WITH 1 INCREMENT BY 1 CACHE 20 NOCYCLE;
CREATE SEQUENCE SEQ_SGP_POSITION           START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SGP_MOUVEMENT          START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- -------------------------------------------------------------------------------------
-- 2. Habilitations (SM_USER -> profil)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_HABILITATION (
  ID_HABILITATION   NUMBER(19)          NOT NULL,
  UTILISATEUR       VARCHAR2(30 CHAR)   NOT NULL,   -- identifiant SiteMinder : numero client ou matricule
  PROFIL            VARCHAR2(15 CHAR)   NOT NULL,   -- CLIENT, CONSEILLER, BACK_OFFICE, ADMIN, SYSTEME
  NUM_CLIENT        VARCHAR2(8 CHAR),               -- renseigne pour le profil CLIENT
  ACTIF             NUMBER(1)           DEFAULT 1 NOT NULL,
  DT_CREATION       TIMESTAMP           NOT NULL,
  DT_MAJ            TIMESTAMP,
  USR_MAJ           VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_HABILITATION PRIMARY KEY (ID_HABILITATION) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_HABILITATION_USR UNIQUE (UTILISATEUR) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT CK_SGP_HABILITATION_PROFIL CHECK (PROFIL IN ('CLIENT','CONSEILLER','BACK_OFFICE','ADMIN','SYSTEME')),
  CONSTRAINT CK_SGP_HABILITATION_ACTIF CHECK (ACTIF IN (0,1))
) TABLESPACE SIGEP_DATA;

-- -------------------------------------------------------------------------------------
-- 3. Clients (recopie du RCC a l'ouverture du premier plan)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_CLIENT (
  ID_CLIENT           NUMBER(19)          NOT NULL,
  NUM_CLIENT          VARCHAR2(8 CHAR)    NOT NULL,   -- numero RCC, 8 chiffres
  CIVILITE            VARCHAR2(3 CHAR),
  NOM                 VARCHAR2(60 CHAR)   NOT NULL,
  PRENOM              VARCHAR2(60 CHAR),
  DT_NAISSANCE        DATE,
  ADR_LIGNE1          VARCHAR2(80 CHAR),
  ADR_LIGNE2          VARCHAR2(80 CHAR),
  ADR_CP              VARCHAR2(10 CHAR),
  ADR_VILLE           VARCHAR2(60 CHAR),
  ADR_PAYS            VARCHAR2(2 CHAR)    DEFAULT 'FR',
  EMAIL               VARCHAR2(120 CHAR),
  RESIDENT_FISCAL_FR  NUMBER(1)           DEFAULT 1,
  DT_CREATION         TIMESTAMP           NOT NULL,
  DT_MAJ              TIMESTAMP,
  USR_MAJ             VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_CLIENT PRIMARY KEY (ID_CLIENT) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_CLIENT_NUM UNIQUE (NUM_CLIENT) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT CK_SGP_CLIENT_RESIDENT CHECK (RESIDENT_FISCAL_FR IN (0,1))
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_CLIENT_NOM ON SGP_CLIENT (UPPER(NOM), UPPER(PRENOM)) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 4. Referentiel valeurs
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_VALEUR (
  ID_VALEUR         NUMBER(19)          NOT NULL,
  CODE_ISIN         VARCHAR2(12 CHAR)   NOT NULL,
  MNEMO             VARCHAR2(10 CHAR),
  LIBELLE           VARCHAR2(80 CHAR)   NOT NULL,
  TYPE_VALEUR       VARCHAR2(10 CHAR)   NOT NULL,   -- ACTION, OPCVM, ETF, DROIT, AUTRE
  PLACE_COTATION    VARCHAR2(4 CHAR),               -- code MIC (XPAR...)
  PAYS_SIEGE        VARCHAR2(2 CHAR),               -- ISO 3166-1 alpha-2
  DEVISE            VARCHAR2(3 CHAR)    DEFAULT 'EUR' NOT NULL,
  ELIGIBLE_PEA      NUMBER(1)           DEFAULT 0 NOT NULL,
  ELIGIBLE_PEA_PME  NUMBER(1)           DEFAULT 0 NOT NULL,
  CODE_FININFO      VARCHAR2(12 CHAR),              -- code valeur fournisseur (rapprochement fichier)
  DT_RADIATION      DATE,
  DT_CREATION       TIMESTAMP           NOT NULL,
  DT_MAJ            TIMESTAMP,
  USR_MAJ           VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_VALEUR PRIMARY KEY (ID_VALEUR) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_VALEUR_ISIN UNIQUE (CODE_ISIN) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT CK_SGP_VALEUR_TYPE CHECK (TYPE_VALEUR IN ('ACTION','OPCVM','ETF','DROIT','AUTRE')),
  CONSTRAINT CK_SGP_VALEUR_ELIG CHECK (ELIGIBLE_PEA IN (0,1) AND ELIGIBLE_PEA_PME IN (0,1))
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_VALEUR_FININFO ON SGP_VALEUR (CODE_FININFO) TABLESPACE SIGEP_IDX;
CREATE INDEX IX_SGP_VALEUR_MNEMO   ON SGP_VALEUR (UPPER(MNEMO)) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 5. Cours de cloture (1 ligne / valeur / jour ; ~600 valeurs x 250 jours = 150 000 lignes/an)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_COURS (
  ID_COURS      NUMBER(19)          NOT NULL,
  ID_VALEUR     NUMBER(19)          NOT NULL,
  DT_COURS      DATE                NOT NULL,
  OUVERTURE     NUMBER(15,4),
  CLOTURE       NUMBER(15,4)        NOT NULL,
  PLUS_HAUT     NUMBER(15,4),
  PLUS_BAS      NUMBER(15,4),
  VOLUME        NUMBER(19),
  SOURCE        VARCHAR2(10 CHAR)   DEFAULT 'FININFO' NOT NULL,   -- FININFO ou SAISIE
  DT_CREATION   TIMESTAMP           NOT NULL,
  DT_MAJ        TIMESTAMP,
  USR_MAJ       VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_COURS PRIMARY KEY (ID_COURS) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_COURS_VAL_DT UNIQUE (ID_VALEUR, DT_COURS) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_COURS_VALEUR FOREIGN KEY (ID_VALEUR) REFERENCES SGP_VALEUR (ID_VALEUR)
) TABLESPACE SIGEP_DATA;

-- -------------------------------------------------------------------------------------
-- 6. Coupons (saisis par le BO, traites par le batch a la date de paiement)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_COUPON (
  ID_COUPON         NUMBER(19)          NOT NULL,
  ID_VALEUR         NUMBER(19)          NOT NULL,
  DT_DETACHEMENT    DATE                NOT NULL,
  DT_PAIEMENT       DATE                NOT NULL,
  MONTANT_UNITAIRE  NUMBER(15,4)        NOT NULL,
  LIBELLE           VARCHAR2(100 CHAR),
  TRAITE            NUMBER(1)           DEFAULT 0 NOT NULL,
  DT_CREATION       TIMESTAMP           NOT NULL,
  DT_MAJ            TIMESTAMP,
  USR_MAJ           VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_COUPON PRIMARY KEY (ID_COUPON) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_COUPON_VALEUR FOREIGN KEY (ID_VALEUR) REFERENCES SGP_VALEUR (ID_VALEUR),
  CONSTRAINT CK_SGP_COUPON_TRAITE CHECK (TRAITE IN (0,1))
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_COUPON_PAIEMENT ON SGP_COUPON (DT_PAIEMENT, TRAITE) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 7. Comptes especes (1 par plan)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_COMPTE_ESPECES (
  ID_COMPTE     NUMBER(19)          NOT NULL,
  NUM_COMPTE    VARCHAR2(23 CHAR)   NOT NULL,   -- 17515 + agence(5) + 9 chiffres du plan + '00'
  SOLDE         NUMBER(15,2)        DEFAULT 0 NOT NULL,
  DEVISE        VARCHAR2(3 CHAR)    DEFAULT 'EUR' NOT NULL,
  DT_CREATION   TIMESTAMP           NOT NULL,
  DT_MAJ        TIMESTAMP,
  USR_MAJ       VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_COMPTE_ESPECES PRIMARY KEY (ID_COMPTE) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_COMPTE_ESPECES_NUM UNIQUE (NUM_COMPTE) USING INDEX TABLESPACE SIGEP_IDX
) TABLESPACE SIGEP_DATA;

-- -------------------------------------------------------------------------------------
-- 8. Plans
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_PLAN (
  ID_PLAN                NUMBER(19)          NOT NULL,
  NUM_PLAN               VARCHAR2(12 CHAR)   NOT NULL,   -- 'PEA' + 9 chiffres (format v1)
  TYPE_PLAN              VARCHAR2(10 CHAR)   NOT NULL,   -- PEA, PEA_PME
  ID_CLIENT              NUMBER(19)          NOT NULL,
  DT_OUVERTURE           DATE                NOT NULL,
  DT_PREMIER_VERSEMENT   DATE,                           -- NULL pour les plans repris de v1
  DT_CLOTURE             DATE,
  STATUT                 NUMBER(1)           DEFAULT 0 NOT NULL,   -- ORDINAL : 0 OUVERT, 1 CLOTURE, 2 TRANSFERE
  TOTAL_VERSEMENTS       NUMBER(15,2)        DEFAULT 0 NOT NULL,   -- denormalise, tenu par l'application
  ID_COMPTE_ESPECES      NUMBER(19)          NOT NULL,
  DT_CREATION            TIMESTAMP           NOT NULL,
  DT_MAJ                 TIMESTAMP,
  USR_MAJ                VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_PLAN PRIMARY KEY (ID_PLAN) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_PLAN_NUM UNIQUE (NUM_PLAN) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_PLAN_COMPTE UNIQUE (ID_COMPTE_ESPECES) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_PLAN_CLIENT FOREIGN KEY (ID_CLIENT) REFERENCES SGP_CLIENT (ID_CLIENT),
  CONSTRAINT FK_SGP_PLAN_COMPTE FOREIGN KEY (ID_COMPTE_ESPECES) REFERENCES SGP_COMPTE_ESPECES (ID_COMPTE),
  CONSTRAINT CK_SGP_PLAN_TYPE CHECK (TYPE_PLAN IN ('PEA','PEA_PME')),
  CONSTRAINT CK_SGP_PLAN_STATUT CHECK (STATUT IN (0,1,2))
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_PLAN_CLIENT ON SGP_PLAN (ID_CLIENT) TABLESPACE SIGEP_IDX;
CREATE INDEX IX_SGP_PLAN_STATUT ON SGP_PLAN (STATUT) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 9. Operations especes
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_OPERATION_ESPECES (
  ID_OPERATION    NUMBER(19)          NOT NULL,
  ID_COMPTE       NUMBER(19)          NOT NULL,
  TYPE_OPERATION  VARCHAR2(25 CHAR)   NOT NULL,   -- VERSEMENT, RETRAIT, ACHAT_TITRES, VENTE_TITRES, DIVIDENDE,
                                                  -- FRAIS, REGULARISATION, PRELEVEMENTS_SOCIAUX
  MONTANT         NUMBER(15,2)        NOT NULL,   -- signe : + credit, - debit
  DT_OPERATION    TIMESTAMP           NOT NULL,
  DT_VALEUR       DATE                NOT NULL,
  LIBELLE         VARCHAR2(100 CHAR),
  REFERENCE       VARCHAR2(30 CHAR),              -- reference du mouvement titres / retrait / coupon
  SOLDE_APRES     NUMBER(15,2),
  DT_CREATION     TIMESTAMP           NOT NULL,
  DT_MAJ          TIMESTAMP,
  USR_MAJ         VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_OPERATION_ESPECES PRIMARY KEY (ID_OPERATION) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_OPERATION_COMPTE FOREIGN KEY (ID_COMPTE) REFERENCES SGP_COMPTE_ESPECES (ID_COMPTE)
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_OPERATION_COMPTE_DT ON SGP_OPERATION_ESPECES (ID_COMPTE, DT_OPERATION) TABLESPACE SIGEP_IDX;
CREATE INDEX IX_SGP_OPERATION_REF        ON SGP_OPERATION_ESPECES (REFERENCE) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 10. Positions (1 ligne / plan / valeur)
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_POSITION (
  ID_POSITION   NUMBER(19)      NOT NULL,
  ID_PLAN       NUMBER(19)      NOT NULL,
  ID_VALEUR     NUMBER(19)      NOT NULL,
  QUANTITE      NUMBER(18,3)    DEFAULT 0 NOT NULL,   -- milliemes de part pour les OPCVM
  PRU           NUMBER(15,4)    DEFAULT 0 NOT NULL,   -- prix de revient unitaire, frais inclus
  DT_CREATION   TIMESTAMP       NOT NULL,
  DT_MAJ        TIMESTAMP,
  USR_MAJ       VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_POSITION PRIMARY KEY (ID_POSITION) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_POSITION UNIQUE (ID_PLAN, ID_VALEUR) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_POSITION_PLAN FOREIGN KEY (ID_PLAN) REFERENCES SGP_PLAN (ID_PLAN),
  CONSTRAINT FK_SGP_POSITION_VALEUR FOREIGN KEY (ID_VALEUR) REFERENCES SGP_VALEUR (ID_VALEUR)
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_POSITION_VALEUR ON SGP_POSITION (ID_VALEUR) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 11. Mouvements titres
-- -------------------------------------------------------------------------------------
CREATE TABLE SGP_MOUVEMENT (
  ID_MOUVEMENT      NUMBER(19)          NOT NULL,
  REFERENCE         VARCHAR2(20 CHAR)   NOT NULL,   -- 'MVT' + AAAAMMJJ + 6 chiffres
  ID_PLAN           NUMBER(19)          NOT NULL,
  ID_VALEUR         NUMBER(19)          NOT NULL,
  SENS              VARCHAR2(5 CHAR)    NOT NULL,   -- ACHAT, VENTE
  QUANTITE          NUMBER(18,3)        NOT NULL,
  COURS_EXECUTION   NUMBER(15,4),
  MONTANT_BRUT      NUMBER(15,2),
  FRAIS             NUMBER(15,2),
  MONTANT_NET       NUMBER(15,2),                   -- brut + frais a l'achat, brut - frais a la vente
  DT_MOUVEMENT      TIMESTAMP           NOT NULL,
  DT_REGLEMENT      DATE,                           -- J+2 ouvres
  STATUT            VARCHAR2(10 CHAR)   DEFAULT 'SAISI' NOT NULL,   -- SAISI, EXECUTE, REGLE, ANNULE
  CANAL             VARCHAR2(10 CHAR),              -- WEB, AGENCE
  MOTIF_ANNULATION  VARCHAR2(200 CHAR),
  DT_CREATION       TIMESTAMP           NOT NULL,
  DT_MAJ            TIMESTAMP,
  USR_MAJ           VARCHAR2(30 CHAR),
  CONSTRAINT PK_SGP_MOUVEMENT PRIMARY KEY (ID_MOUVEMENT) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_MOUVEMENT_REF UNIQUE (REFERENCE) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT FK_SGP_MOUVEMENT_PLAN FOREIGN KEY (ID_PLAN) REFERENCES SGP_PLAN (ID_PLAN),
  CONSTRAINT FK_SGP_MOUVEMENT_VALEUR FOREIGN KEY (ID_VALEUR) REFERENCES SGP_VALEUR (ID_VALEUR),
  CONSTRAINT CK_SGP_MOUVEMENT_SENS CHECK (SENS IN ('ACHAT','VENTE')),
  CONSTRAINT CK_SGP_MOUVEMENT_STATUT CHECK (STATUT IN ('SAISI','EXECUTE','REGLE','ANNULE'))
) TABLESPACE SIGEP_DATA;

CREATE INDEX IX_SGP_MOUVEMENT_PLAN_DT ON SGP_MOUVEMENT (ID_PLAN, DT_MOUVEMENT) TABLESPACE SIGEP_IDX;
CREATE INDEX IX_SGP_MOUVEMENT_STATUT  ON SGP_MOUVEMENT (STATUT) TABLESPACE SIGEP_IDX;

-- -------------------------------------------------------------------------------------
-- 12. Droits de lecture pour le decisionnel
-- -------------------------------------------------------------------------------------
GRANT SELECT ON SIGEP_APP.SGP_HABILITATION      TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_CLIENT            TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_VALEUR            TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_COURS             TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_COUPON            TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_COMPTE_ESPECES    TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_PLAN              TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_OPERATION_ESPECES TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_POSITION          TO SIGEP_LECTURE;
GRANT SELECT ON SIGEP_APP.SGP_MOUVEMENT         TO SIGEP_LECTURE;

-- -------------------------------------------------------------------------------------
-- 13. Habilitations techniques minimales (le reste vient de la reprise v1)
-- -------------------------------------------------------------------------------------
INSERT INTO SGP_HABILITATION (ID_HABILITATION, UTILISATEUR, PROFIL, NUM_CLIENT, ACTIF, DT_CREATION, DT_MAJ, USR_MAJ)
VALUES (SEQ_SGP_HABILITATION.NEXTVAL, 'U010007', 'ADMIN', NULL, 1, SYSTIMESTAMP, SYSTIMESTAMP, 'DBA');
INSERT INTO SGP_HABILITATION (ID_HABILITATION, UTILISATEUR, PROFIL, NUM_CLIENT, ACTIF, DT_CREATION, DT_MAJ, USR_MAJ)
VALUES (SEQ_SGP_HABILITATION.NEXTVAL, 'U030001', 'BACK_OFFICE', NULL, 1, SYSTIMESTAMP, SYSTIMESTAMP, 'DBA');

COMMIT;

-- Controle
SELECT table_name FROM all_tables WHERE owner = 'SIGEP_APP' ORDER BY 1;
SELECT sequence_name FROM all_sequences WHERE sequence_owner = 'SIGEP_APP' ORDER BY 1;

-- fin du script
