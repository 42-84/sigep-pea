# SIGEP - Journal des versions

Format : version - date de mise en production - tickets Jira (jira.bas.intra/browse/SIGEP-nnn).
Les versions de correctif (x.y.z) sont listées quand elles ont été déployées en production.

## 2.0.1 - 04/04/2016

- Correctifs post-reprise : troncature de `USR_MAJ` à 30 caractères (identifiants SiteMinder
  longs), numéros de plan de la reprise sans zéro de tête.
- Date de règlement-livraison à J+2 ouvrés (T2S) - le code de la reprise avait J+3.

## 2.0.0 - 14/03/2016

**Mise en production de la refonte** (SIGEP v2). Reprise des données de SIGEP v1 (Struts 1 /
EJB 2 / Oracle 9i) le week-end des 12-13/03/2016 : 38 412 plans, 41 003 comptes espèces,
positions et habilitations. L'historique des opérations d'avant 2016 n'est pas repris opération
par opération (`TOTAL_VERSEMENTS` recopié, `USR_MAJ = 'REPRISE'`).

- Trois services Spring Boot 1.5 / Java 8 : sigep-referentiel (8081), sigep-comptes (8082),
  sigep-portefeuille (8083), base Oracle 11g commune (`SIGEP_APP`).
- Authentification par en-tête `SM_USER` (reverse proxy Apache + SiteMinder), habilitations en base.
- Import quotidien des cours Fininfo (format COTATIONS V2) à 18h30.
- Ouverture, versements, retraits (règle des 8 ans), clôture ; ordres exécutés au dernier cours
  de clôture connu ; PRU au coût moyen pondéré frais inclus ; coupons ; contrôle nocturne des soldes.
- Schéma initial : `db/scripts/2016-03_V2.0.0_creation_schema.sql`.
