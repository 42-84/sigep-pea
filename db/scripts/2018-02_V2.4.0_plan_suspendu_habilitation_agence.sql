-- =====================================================================================
-- SIGEP - Version 2.4.0 - Statut SUSPENDU et code agence (SIGEP-350, SIGEP-355)
-- Auteur : S. Nguyen (DBA) / F. Dubois - 06/02/2018
-- Execution : sqlplus SIGEP_APP@SIGEPxxx @2018-02_V2.4.0_plan_suspendu_habilitation_agence.sql
--
-- 1. Nouveau statut de plan SUSPENDU (succession, saisie) : valeur ordinale 3, ajoutee EN FIN
--    de l'enumeration StatutPlan (cf. incident SIGEP-231 du 12/09/2017 : ne jamais reordonner).
--    Seule la contrainte CHECK change, la colonne STATUT NUMBER(1) reste.
-- 2. Code agence sur le plan et sur l'habilitation, pour la future restriction du conseiller
--    a son agence (controle applicatif non livre dans cette version).
-- =====================================================================================

SET ECHO ON
WHENEVER SQLERROR EXIT SQL.SQLCODE

-- 1. Statut SUSPENDU (ordinal 3)
ALTER TABLE SGP_PLAN DROP CONSTRAINT CK_SGP_PLAN_STATUT;
ALTER TABLE SGP_PLAN ADD CONSTRAINT CK_SGP_PLAN_STATUT CHECK (STATUT IN (0,1,2,3));
COMMENT ON COLUMN SGP_PLAN.STATUT IS 'Ordinal StatutPlan : 0 OUVERT, 1 CLOTURE, 2 TRANSFERE, 3 SUSPENDU. Ne pas reordonner (SIGEP-231).';

-- 2. Code agence
ALTER TABLE SGP_PLAN ADD (AGENCE VARCHAR2(5 CHAR));
COMMENT ON COLUMN SGP_PLAN.AGENCE IS 'Code agence gestionnaire (5 chiffres, ex. 17001)';

ALTER TABLE SGP_HABILITATION ADD (AGENCE VARCHAR2(5 CHAR));
COMMENT ON COLUMN SGP_HABILITATION.AGENCE IS 'Agence de rattachement du conseiller ; agence du client pour le profil CLIENT';

CREATE INDEX IX_SGP_PLAN_AGENCE ON SGP_PLAN (AGENCE, STATUT) TABLESPACE SIGEP_IDX;

-- 3. Reprise : agence deduite du numero de compte especes (positions 6 a 10) pour les plans existants
UPDATE SGP_PLAN p
   SET p.AGENCE = (SELECT SUBSTR(c.NUM_COMPTE, 6, 5) FROM SGP_COMPTE_ESPECES c WHERE c.ID_COMPTE = p.ID_COMPTE_ESPECES)
 WHERE p.AGENCE IS NULL;

-- les comptes repris de v1 avec agence '00000' (inconnue) : laisser NULL
UPDATE SGP_PLAN SET AGENCE = NULL WHERE AGENCE = '00000';

-- agence des conseillers : fichier RH fourni par la MOA, charge par SQL*Loader (hors script)
-- agence des clients : celle de leur premier plan
UPDATE SGP_HABILITATION h
   SET h.AGENCE = (SELECT MIN(p.AGENCE) FROM SGP_PLAN p JOIN SGP_CLIENT c ON c.ID_CLIENT = p.ID_CLIENT
                    WHERE c.NUM_CLIENT = h.NUM_CLIENT)
 WHERE h.PROFIL = 'CLIENT' AND h.AGENCE IS NULL;

-- Controle avant COMMIT
SELECT AGENCE, COUNT(*) FROM SGP_PLAN GROUP BY AGENCE ORDER BY 2 DESC;
SELECT PROFIL, COUNT(*), COUNT(AGENCE) FROM SGP_HABILITATION GROUP BY PROFIL;

COMMIT;

-- NB : la 2.4.0 ajoute aussi SGP_PLAN.COMMENTAIRE VARCHAR2(500). Oublie dans ce script ; cree par
-- Hibernate (ddl-auto=update) au premier demarrage de la version, en VARCHAR2(500 CHAR). Laisse tel quel.
