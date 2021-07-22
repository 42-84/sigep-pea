-- =====================================================================================
-- SIGEP - Version 2.7.3 - Radiation de NATIXIS (FR0000120685) - SIGEP-528
-- Auteur : TMA SIGEP / S. Nguyen (DBA) - 22/07/2021
-- Execution : sqlplus SIGEP_APP@SIGEPxxx @2021-07_V2.7.3_radiation_natixis.sql
--
-- Contexte : offre publique de retrait BPCE, retrait de la cote Euronext Paris le 21/07/2021.
-- Script de DONNEES uniquement (pas de DDL). L'ecran de radiation du BO (PUT
-- /api/valeurs/{isin}/radiation) aurait fait la meme chose, mais le BO a demande un passage
-- DBA pour avoir la trace et parce que 12 plans detiennent encore la valeur.
-- =====================================================================================

SET ECHO ON
WHENEVER SQLERROR EXIT SQL.SQLCODE

-- Etat avant
SELECT ID_VALEUR, CODE_ISIN, LIBELLE, ELIGIBLE_PEA, ELIGIBLE_PEA_PME, DT_RADIATION
  FROM SGP_VALEUR WHERE CODE_ISIN = 'FR0000120685';

-- Plans detenteurs (liste transmise au BO Titres pour traitement des positions residuelles)
SELECT p.NUM_PLAN, c.NUM_CLIENT, c.NOM, po.QUANTITE, po.PRU, p.AGENCE
  FROM SGP_POSITION po
  JOIN SGP_VALEUR v ON v.ID_VALEUR = po.ID_VALEUR
  JOIN SGP_PLAN p ON p.ID_PLAN = po.ID_PLAN
  JOIN SGP_CLIENT c ON c.ID_CLIENT = p.ID_CLIENT
 WHERE v.CODE_ISIN = 'FR0000120685' AND po.QUANTITE > 0
 ORDER BY p.NUM_PLAN;

-- Radiation
UPDATE SGP_VALEUR
   SET DT_RADIATION     = TO_DATE('21/07/2021', 'DD/MM/YYYY'),
       ELIGIBLE_PEA     = 0,
       ELIGIBLE_PEA_PME = 0,
       DT_MAJ           = SYSTIMESTAMP,
       USR_MAJ          = 'U030001'
 WHERE CODE_ISIN = 'FR0000120685'
   AND DT_RADIATION IS NULL;

-- 1 ligne attendue
SELECT SQL%ROWCOUNT FROM DUAL;

-- Le dernier cours Fininfo recu (20/07/2021, 4,03 EUR) reste en base : il sert a valoriser les
-- positions residuelles au prix de l'OPR. Pas de cours apres cette date (la ligne arrive
-- suspendue 'S' dans le fichier, ignoree par l'import).

COMMIT;

-- Etat apres
SELECT ID_VALEUR, CODE_ISIN, LIBELLE, ELIGIBLE_PEA, ELIGIBLE_PEA_PME, DT_RADIATION, USR_MAJ
  FROM SGP_VALEUR WHERE CODE_ISIN = 'FR0000120685';
