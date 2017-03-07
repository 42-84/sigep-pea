# SIGEP v2 - Dossier d'architecture technique

| | |
|---|---|
| Projet | SIGEP - refonte 2016 |
| Auteur | J. Martin (architecte, DSI / Pôle Épargne & Titres) |
| Version | v1.2 - mars 2017 |
| Statut | Validé CAT du 07/04/2016 (v1.0) ; v1.2 : mise à jour après livraison 2.1 |
| Diffusion | équipe SIGEP, MOE Épargne, Exploitation, Sécurité |

Historique des versions

| Version | Date | Auteur | Modifications |
|---|---|---|---|
| 0.9 | 18/01/2016 | JM | Première version pour le CAT |
| 1.0 | 07/04/2016 | JM | Validée. Ajout §7 points ouverts |
| 1.1 | 20/09/2016 | JM | Spring Boot 1.4 → 1.5 (§6). Ports (§5) |
| 1.2 | 15/03/2017 | JM | sigep-fiscalite : décision de fusion (§4). H2 partagée (§3.3) |

---

## 1. Contexte

SIGEP v1 (2004) est une application Struts 1 / EJB 2 déployée sur WebSphere 6, sur Oracle 9i,
qui gère environ 38 000 plans PEA et PEA-PME pour la clientèle particuliers de la BAS. WebSphere 6
n'est plus supporté et le socle Java 1.4 bloque la mise en conformité PCI-DSS du parc.

La refonte a trois objectifs :

1. Sortir de WebSphere et de Struts 1 (fin de support), sans changer les règles de gestion.
2. Découper l'application en services indépendants déployables séparément, dans l'esprit du
   schéma directeur DSI 2015 (« architecture de services »).
3. Reprendre l'intégralité des données v1 (plans, positions, comptes espèces, habilitations)
   sans interruption de service supérieure à un week-end.

Périmètre exclu : la passation d'ordres au marché (reste sur STRATE via fichier), le calcul
d'IFU (reste dans l'outil fiscal groupe), la relation avec le référentiel client central (RCC :
recopie du numéro client, pas de synchronisation).

Volumétrie cible : 40 000 plans, 200 ordres/jour, 2 500 cours/valeur d'historique, 600 valeurs
suivies, pointe de consultation le soir (18h-21h) après publication des cours.

## 2. Principes

- **Un service = un domaine métier = un déploiement.** Chaque service est un exécutable Spring Boot
  autonome (jar exécutable, Tomcat embarqué), lancé en service système sur sa propre machine.
- **Communication REST/JSON** entre services et avec le front, via `RestTemplate`. Pas de bus de
  messages : la volumétrie ne le justifie pas et l'exploitation n'a pas de compétence MQ.
- **Base de données commune en phase transitoire** (§3.3). La séparation en un schéma par service
  est prévue en phase 2.
- **Authentification déléguée** au reverse proxy Apache + agent SiteMinder (SSO groupe). Les
  services font confiance à l'en-tête `SM_USER`.
- **Pas de framework de sécurité applicatif** (Spring Security écarté : SiteMinder fait déjà le
  travail, on ne garde qu'un filtre servlet et une table d'habilitations).
- **Sobriété** : pas d'ORM avancé (pas de second-level cache), pas de Spring Cloud, pas de
  découverte de services (adresses en configuration).

## 3. Vue d'ensemble

### 3.1 Schéma

```
                         Poste client / agence
                                 |
                                 | HTTPS
                                 v
        +------------------------------------------------+
        |  Reverse proxy Apache 2.4 + agent SiteMinder    |   srv-sigep-web-01
        |  (SmAgent : authentification SSO, pose SM_USER) |
        |  / -> front statique (Angular)                  |
        |  /sigep/api/valeurs, /sigep/api/cours   -> ref  |
        |  /sigep/api/clients|plans|comptes-esp.  -> cpt  |
        |  /sigep/api/portefeuilles               -> ptf  |
        +-----------+---------------+--------------+------+
                    |               |              |
        HTTP 8081   |    HTTP 8082  |   HTTP 8083  |
                    v               v              v
     +----------------+  +----------------+  +--------------------+  +----------------+
     | sigep-         |  | sigep-         |  | sigep-             |  | sigep-         |
     | referentiel    |<-| comptes        |<-| portefeuille       |  | fiscalite      |
     |                |  |                |->|                    |  | (phase 1b)     |
     | valeurs, cours,|  | clients, plans,|  | positions, ordres, |  | PS, IFU,       |
     | eligibilite,   |  | comptes        |  | valorisation,      |  | taux           |
     | import Fininfo |  | especes        |  | coupons            |  |                |
     | srv-sigep-ref-01  | srv-sigep-cpt-01  | srv-sigep-ptf-01   |  | (a definir)    |
     +-------+--------+  +-------+--------+  +---------+----------+  +-------+--------+
             |                   |                     |                     |
             +-------------------+----------+----------+---------------------+
                                            |
                                            v  JDBC (ojdbc6, pool Tomcat)
                              +-----------------------------+
                              |  Oracle 11g R2              |
                              |  ora-sigep-prd.bas.intra    |
                              |  schema SIGEP_APP, tables   |
                              |  SGP_*  (base COMMUNE)      |
                              +-----------------------------+

     Flux entrants batch :   CFT  --> /appli/sigep/fininfo/entree/COT_AAAAMMJJ.txt (18h05)
                                       lu par sigep-referentiel a 18h30
```

Le code partagé (entités JPA, repositories, DTO, filtre SiteMinder, gestion des erreurs,
utilitaires) est dans le module `sigep-commun`, dépendance de compilation des services.

### 3.2 Flux principaux

| Flux | Appelant | Appelé | Endpoint |
|---|---|---|---|
| Valorisation d'un plan (synthèse, retrait) | comptes | portefeuille | `GET /api/portefeuilles/{n}/valorisation` |
| Dernier cours d'une valeur | portefeuille | referentiel | `GET /api/cours/{isin}/dernier` |
| Éligibilité d'un titre à l'achat | portefeuille | referentiel | `GET /api/valeurs/{isin}/eligibilite` |
| Solde espèces avant achat | portefeuille | comptes | `GET /api/comptes-especes/{n}/solde` |
| Écriture espèces (achat, vente, coupon) | portefeuille | comptes | `POST /api/comptes-especes/{n}/operations` |
| Prélèvements sociaux d'un retrait | comptes | fiscalite | `GET /api/fiscalite/prelevements` (phase 1b) |

L'utilisateur SiteMinder est propagé d'un service à l'autre dans l'en-tête `SM_USER` par un
intercepteur `RestTemplate` (`ConfigurationRestClient`), afin que la colonne d'audit `USR_MAJ`
du service appelé porte le bon utilisateur. Hors requête HTTP (batchs), l'identifiant technique
`SIGEP-INTERNE` (profil `SYSTEME`) est utilisé.

### 3.3 Base de données commune (phase transitoire)

Décision CAT 07/04/2016 : **les trois services partagent le schéma Oracle `SIGEP_APP`** repris
de la v1 (tables `SGP_*`, préfixe conservé pour la reprise et les requêtes des DBA).

Motifs :

- la reprise de données du 14/03/2016 se fait en une seule passe sur un schéma existant ;
- les jointures inter-domaines (plan ↔ positions ↔ compte espèces) restent possibles pour le
  décisionnel et les extractions réglementaires, le temps de bâtir les API équivalentes ;
- l'équipe n'a pas la taille pour gérer trois bases et leurs sauvegardes.

Règle de discipline associée : **chaque table a un service propriétaire**, seul autorisé à
y écrire. Les autres services y accèdent par l'API du propriétaire.

| Tables | Propriétaire |
|---|---|
| `SGP_VALEUR`, `SGP_COURS`, `SGP_COUPON` | sigep-referentiel |
| `SGP_CLIENT`, `SGP_PLAN`, `SGP_COMPTE_ESPECES`, `SGP_OPERATION_ESPECES` | sigep-comptes |
| `SGP_POSITION`, `SGP_MOUVEMENT`, `SGP_VALORISATION_HISTO` | sigep-portefeuille |
| `SGP_HABILITATION` | commun (lecture seule par tous, écriture par l'ADMIN via sigep-comptes) |

Cette règle est conventionnelle : rien ne l'impose techniquement puisque les repositories sont
tous dans `sigep-commun` et que chaque service voit toutes les entités. *Note v1.2 : deux
lectures croisées ont déjà été acceptées pour tenir les délais (nombre de détenteurs d'une
valeur lu dans `SGP_POSITION` par le référentiel ; solde espèces lu sur l'entité `Plan` par le
portefeuille). Elles seront résorbées en phase 2.*

**Phase 2 (séparation)** : un schéma Oracle par service, suppression des relations JPA
inter-domaines (remplacées par des identifiants), reprise des lectures croisées par API.
Planification : après stabilisation de la 2.x, à l'initiative de la MOE. Non chiffrée.

En développement, la base commune est un fichier H2 unique en mode `AUTO_SERVER` ouvert par les
trois services (voir `docs/INSTALL.txt`).

## 4. Découpage en services

Quatre services étaient prévus au CAT.

### 4.1 sigep-referentiel

Valeurs mobilières (actions, OPCVM, ETF, droits), cours de clôture quotidiens, éligibilité PEA
et PEA-PME, import du fichier fournisseur Fininfo (format « COTATIONS V2 », largeur fixe,
`docs/specs/FORMAT_FININFO.txt`). Saisie manuelle de cours pour les OPCVM maison non couverts.
Batchs : import 18h30 + rattrapage 19h30 (lundi-vendredi).

### 4.2 sigep-comptes

Clients (recopie RCC), plans (ouverture, versements, retraits, clôture, statuts), comptes
espèces et opérations, contrôle nocturne des soldes (02h15). Règles d'ancienneté du plan
(8 ans à la rédaction de ce document).

### 4.3 sigep-portefeuille

Positions par plan et par valeur, PRU (coût moyen pondéré frais inclus), ordres d'achat/vente
exécutés au dernier cours connu (exécution simulée, l'interface STRATE temps réel étant hors
périmètre), barème de frais, valorisation à la demande et photo quotidienne (20h00), paiement
des coupons (06h30).

### 4.4 sigep-fiscalite

Prélèvements sociaux à la sortie (taux par année), part de gain d'un retrait partiel, taux
d'imposition avant 5 ans, préparation de l'IFU. Service prévu en **phase 1b, à faire au T3 2017**,
sur une machine `srv-sigep-fis-01` à demander à l'exploitation. En attendant, le calcul des
prélèvements est porté par une classe de service de sigep-comptes.

> **Mise à jour v1.2 (15/03/2017)** : la MOE a décidé de ne pas construire sigep-fiscalite.
> Le périmètre est trop réduit (trois méthodes de calcul, une table de taux) pour justifier un
> déploiement et une machine supplémentaires, et l'IFU reste dans l'outil groupe. La classe
> `FiscaliteService` reste dans sigep-comptes de façon définitive. Le module est retiré de
> `settings.gradle` (ligne commentée conservée). Le §4.4 et le flux correspondant du §3.2 sont
> conservés pour l'historique de la décision.

## 5. Communication inter-services et duplication assumée des clients REST

Chaque service qui appelle un autre service possède sa propre classe cliente
(`ReferentielClient`, `ComptesClient` dans sigep-portefeuille ; `PortefeuilleClient` dans
sigep-comptes), qui hérite de `ClientRestAbstrait` (sigep-commun) pour la gestion du
`RestTemplate`, de l'URL de base et de la traduction du 404 en `RessourceInconnueException`.

**La duplication est assumée** : nous avons écarté un module « API client » partagé qui aurait
contenu tous les clients de tous les services. Motifs :

- un client ne doit exposer que ce dont le service appelant a besoin (le portefeuille n'a pas à
  connaître l'ouverture de plan) ;
- un module partagé recrée un couplage de déploiement : toute évolution d'une API force à
  relivrer tous les services ;
- les DTO échangés sont eux, en revanche, partagés dans `sigep-commun/dto` (une seule définition
  de `ValorisationDTO`, `CoursDTO`, `SoldeDTO`...).

Adresses des services partenaires en configuration (`sigep.referentiel.url`, `sigep.comptes.url`,
`sigep.portefeuille.url`), différentes par profil. Pas de découverte de services.

Dépendance circulaire acceptée entre sigep-comptes et sigep-portefeuille (comptes demande la
valorisation, portefeuille demande le solde) : les deux services doivent être démarrés pour
qu'un retrait ou un achat aboutisse.

### Ports et machines

| Service | Machine prod | Port | Machine recette |
|---|---|---|---|
| Reverse proxy Apache / SiteMinder | srv-sigep-web-01.bas.intra | 443 | srv-sigep-rec-01 |
| sigep-referentiel | srv-sigep-ref-01.bas.intra | 8081 | srv-sigep-rec-01 |
| sigep-comptes | srv-sigep-cpt-01.bas.intra | 8082 | srv-sigep-rec-01 |
| sigep-portefeuille | srv-sigep-ptf-01.bas.intra | 8083 | srv-sigep-rec-01 |
| sigep-fiscalite | srv-sigep-fis-01 (à demander) | 8084 | - |
| Oracle | ora-sigep-prd.bas.intra:1521/SIGEPPRD | 1521 | ora-sigep-rec.bas.intra/SIGEPREC |

En recette les trois services tournent sur la même machine.

Les ports 808x ne sont ouverts qu'au reverse proxy et aux autres machines SIGEP (règle
pare-feu SIGEP-FW-01). Les endpoints `/health`, `/info`, `/manage` (actuator) sont exemptés du
filtre SiteMinder pour la supervision.

## 6. Choix techniques

| Sujet | Choix | Commentaire |
|---|---|---|
| Langage | Java 8 | Version validée DSI (8u92). Lambdas peu utilisées : l'équipe vient de Java 5. |
| Socle | Spring Boot **1.4** prévu → **1.5** réalisé | 1.5.1 sorti en janvier 2017 avant la 2.1 ; on suit la dernière 1.5.x. Pas de 2.x avant Java 11 côté serveurs. |
| Build | Gradle (multi-projets) | Choix DSI 2015. Pas de wrapper au départ. |
| Persistance | Spring Data JPA / Hibernate 5, dialecte Oracle10g | Hibernate `ddl-auto=update` en toutes phases, scripts DBA en référence (`db/scripts`). |
| Pool JDBC | Tomcat JDBC (défaut Boot 1.x), 20 connexions par service | validation `SELECT 1 FROM DUAL`. |
| JSON | Jackson + module Hibernate5 | Les entités JPA sont sérialisées directement dans plusieurs API (choix de délai). Dates au format `dd/MM/yyyy`. |
| Sécurité | Filtre servlet `SiteMinderFilter` + table `SGP_HABILITATION` | Profils `CLIENT < CONSEILLER < BACK_OFFICE < ADMIN < SYSTEME` (ordre = niveau). |
| Batchs | `@Scheduled` Spring | Pas de Spring Batch ni d'ordonnanceur externe ($U réservé aux mainframes). Chaque service porte ses batchs. Attention en cas de second nœud : pas de verrou. |
| Import fichier | Lecture ligne à ligne, une transaction par cours | Rejeu idempotent. |
| Logs | Logback, fichiers `/var/log/sigep/sigep-<service>.log` | Rotation par l'exploitation (logrotate). |
| Supervision | actuator `/health` | Interrogé par Centreon toutes les 2 minutes. |
| Front | à définir (phase 1b) | Écrans Struts v1 conservés en attendant sur WebSphere, en lecture seule après la reprise. |
| Base | Oracle 11g R2 | Migration 12c à l'initiative des DBA. |

## 7. Points ouverts

| # | Sujet | Décision attendue | Statut |
|---|---|---|---|
| PO-1 | **Timeouts RestTemplate** : aucun timeout configuré, un service partenaire lent bloque les threads Tomcat de l'appelant. Valeur proposée : 5 s connexion / 30 s lecture. | MOE + exploitation | ouvert |
| PO-2 | **Cache des cours** dans sigep-portefeuille : la valorisation fait un appel HTTP par ligne ; à 40 000 plans le batch du soir est estimé à 30-40 min. Cache local avec invalidation à la fin de l'import Fininfo ? | équipe | ouvert |
| PO-3 | **Calendrier Euronext** pour la date de règlement-livraison (J+2 ouvrés) : `DateUtils` ne connaît que les week-ends, pas les jours fériés de la place. Source : fichier annuel Euronext ou saisie BO ? | MOA | ouvert |
| PO-4 | **Restriction du conseiller à son agence** : le profil CONSEILLER voit tous les plans. La colonne `AGENCE` est prévue sur `SGP_PLAN`. La MOA doit dire si un conseiller d'une autre agence peut dépanner. | MOA | ouvert |
| PO-5 | Référence de mouvement générée en mémoire (compteur par JVM) : à remplacer par une séquence Oracle avant tout déploiement multi-nœuds. | équipe | ouvert |
| PO-6 | Coupons : détention appréciée à la date de traitement et non de détachement. | MOA | ouvert |
| PO-7 | Passage de `ddl-auto=update` à `validate` en production, une fois les scripts DBA alignés. | DBA | ouvert |

*Aucun de ces points n'est bloquant pour la mise en production 2.0.0.*

## 8. Références

- Schéma directeur DSI 2015 - « architecture de services »
- SFD SIGEP v1.4 (02/02/2016) - `docs/specs/SFD_SIGEP_v1.4.txt`
- Interface COTATIONS V2 Fininfo (2009) - `docs/specs/FORMAT_FININFO.txt`
- Règle pare-feu SIGEP-FW-01 (Sécurité, 03/2016)
- Confluence : http://confluence.bas.intra/display/SIGEP/Architecture
