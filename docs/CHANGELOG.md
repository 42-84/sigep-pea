# SIGEP - Journal des versions

Format : version - date de mise en production - tickets Jira (jira.bas.intra/browse/SIGEP-nnn).
Les versions de correctif (x.y.z) sont listées quand elles ont été déployées en production.

## 2.4.0 - 26/02/2018

- **Statut `SUSPENDU`** sur les plans (succession, saisie) : valeur ordinale 3, ajoutée en fin
  d'énumération. Clôture possible depuis SUSPENDU. (SIGEP-355)
- Colonne `AGENCE` sur `SGP_PLAN` et `SGP_HABILITATION` en préparation de la restriction du
  conseiller à son agence (contrôle non livré, cf. TODO). Script
  `2018-02_V2.4.0_plan_suspendu_habilitation_agence.sql`. (SIGEP-350)
- Clôture : si sigep-portefeuille est injoignable, le contrôle « plus de titres » est sauté avec
  une trace WARN (décision MOE). (SIGEP-361)
- Annulation d'un mouvement du jour par le BO (`POST .../mouvements/{ref}/annulation`), sans
  recalcul du PRU à l'annulation d'un achat. (SIGEP-333)

## 2.3.0 - 18/10/2017

- **Correctif incident du 12/09/2017 (SIGEP-231)** : le statut du plan est stocké en ordinal
  (`NUMBER(1)`) ; une réorganisation de l'énumération `StatutPlan` livrée en 2.2.1 avait décalé
  les valeurs et fait repasser tous les plans clôturés en OUVERT. Données corrigées par script DBA
  le 13/09, énumération remise dans l'ordre d'origine avec avertissement dans le code. Décision de
  **ne pas** passer en `EnumType.STRING` (reprise de 40 000 lignes + requêtes JPQL sur `statut = 0`).
- `@Transactional` sur `PlanControleur` pour corriger des `LazyInitializationException` à la
  sérialisation de `client.getPlans()`. (SIGEP-240)
- Synchronisation du formatage de dates dans `DateUtils` (dates fantaisistes en charge, SIGEP-187).

## 2.2.1 - 04/09/2017

- Réorganisation de `StatutPlan` (alphabétique) - **retirée en 2.3.0**, voir SIGEP-231.
- Recherche de clients par nom + prénom.

## 2.2.0 - 19/06/2017

- **Valorisation historisée** : table `SGP_VALORISATION_HISTO` alimentée chaque soir à 20h00 par
  le batch `CalculValorisationQuotidienne` (base du relevé annuel). Script
  `2017-06_V2.2.0_valorisation_histo.sql`. (SIGEP-160)
- Rattrapage automatique de l'import Fininfo à 19h30 si rien n'a été importé dans la journée.
- Abandon du service sigep-fiscalite (fusionné dans sigep-comptes, `docs/ARCHITECTURE.md` §4).

## 2.1.0 - 21/11/2016

- Import manuel d'un fichier Fininfo par l'API (`POST /api/cours/import`, profil BACK_OFFICE) pour
  le rejeu par l'exploitation. (SIGEP-095)
- Saisie manuelle de cours pour les OPCVM maison non couverts par Fininfo (`POST /api/cours/saisie`),
  jamais écrasée par l'import. (SIGEP-088)
- Endpoint `GET /api/cours/etat`.
- Paiement des coupons : arrondi au centime par plan. (SIGEP-058 reste ouvert sur la date de
  détention.)

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
