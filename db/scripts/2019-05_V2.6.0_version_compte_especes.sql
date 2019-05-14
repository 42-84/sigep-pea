-- =====================================================================================
-- SIGEP - Version 2.6.0 - Verrou optimiste sur le compte especes (SIGEP-438)
-- Auteur : S. Nguyen (DBA) / F. Dubois - 13/05/2019
-- Execution : sqlplus SIGEP_APP@SIGEPxxx @2019-05_V2.6.0_version_compte_especes.sql
--
-- Colonne VERSION geree par Hibernate (@Version) : incrementee a chaque UPDATE, l'ecriture
-- concurrente echoue (HTTP 409 CONFLIT). Corrige les doubles debits entre sigep-comptes et
-- sigep-portefeuille qui ecrivent tous les deux sur cette table.
--
-- ATTENTION : a passer AVANT le deploiement de la 2.6.0. Si le service demarre avant,
-- Hibernate cree la colonne lui-meme (NUMBER(19), nullable, sans DEFAULT) et toutes les lignes
-- existantes ont VERSION NULL -> ObjectOptimisticLockingFailureException sur le premier UPDATE
-- de chaque compte. C'est ce qui s'est passe en recette le 14/05 (voir HISTORIQUE.txt).
-- =====================================================================================

SET ECHO ON
WHENEVER SQLERROR EXIT SQL.SQLCODE

ALTER TABLE SGP_COMPTE_ESPECES ADD (VERSION NUMBER(19) DEFAULT 0 NOT NULL);

COMMENT ON COLUMN SGP_COMPTE_ESPECES.VERSION IS 'Verrou optimiste JPA (@Version). Ne pas modifier a la main.';

-- Si la colonne existe deja (creee par Hibernate) :
--   UPDATE SGP_COMPTE_ESPECES SET VERSION = 0 WHERE VERSION IS NULL;
--   ALTER TABLE SGP_COMPTE_ESPECES MODIFY (VERSION DEFAULT 0 NOT NULL);

SELECT COUNT(*) NB_COMPTES, COUNT(VERSION) NB_VERSION_RENSEIGNEE FROM SGP_COMPTE_ESPECES;

COMMIT;
