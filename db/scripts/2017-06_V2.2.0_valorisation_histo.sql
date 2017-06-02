-- =====================================================================================
-- SIGEP - Version 2.2.0 - Historisation de la valorisation quotidienne (SIGEP-160)
-- Auteur : M. Aubert (DBA) sur demande F. Dubois - 30/05/2017
-- Execution : sqlplus SIGEP_APP@SIGEPxxx @2017-06_V2.2.0_valorisation_histo.sql
--
-- Table volumineuse : 1 ligne par plan ouvert et par jour de bourse
-- (~40 000 x 250 = 10 M lignes/an). Pas de FK vers SGP_PLAN (ID brut, choix appli).
-- Sequence en INCREMENT BY 50 : allocationSize = 50 cote JPA, les deux doivent rester alignes.
-- =====================================================================================

SET ECHO ON
WHENEVER SQLERROR EXIT SQL.SQLCODE

CREATE SEQUENCE SEQ_SGP_VALORISATION_HISTO START WITH 1 INCREMENT BY 50 CACHE 20 NOCYCLE;

CREATE TABLE SGP_VALORISATION_HISTO (
  ID_VALO            NUMBER(19)      NOT NULL,
  ID_PLAN            NUMBER(19)      NOT NULL,
  DT_VALORISATION    DATE            NOT NULL,
  VALO_TITRES        NUMBER(15,2)    NOT NULL,
  SOLDE_ESPECES      NUMBER(15,2)    NOT NULL,
  TOTAL_VERSEMENTS   NUMBER(15,2)    NOT NULL,
  CONSTRAINT PK_SGP_VALORISATION_HISTO PRIMARY KEY (ID_VALO) USING INDEX TABLESPACE SIGEP_IDX,
  CONSTRAINT UK_SGP_VALO_PLAN_DT UNIQUE (ID_PLAN, DT_VALORISATION) USING INDEX TABLESPACE SIGEP_IDX
) TABLESPACE SIGEP_DATA
  PCTFREE 0;   -- insertion seule, jamais de mise a jour en dehors du rejeu du jour

-- Pas de colonnes d'audit DT_CREATION / DT_MAJ / USR_MAJ : l'entite ValorisationHisto n'herite
-- pas d'EntiteAuditable. [note FD 2019 : c'etait un oubli, jamais corrige]

-- Partitionnement par annee envisage (option Partitioning non licenciee sur SIGEPPRD) : non.
-- Purge : a definir (releve annuel = besoin de 12 mois glissants + 31/12 de chaque annee).

GRANT SELECT ON SIGEP_APP.SGP_VALORISATION_HISTO TO SIGEP_LECTURE;

COMMIT;
