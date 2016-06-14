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
---

## 1. Contexte

SIGEP v1 (2004) est une application Struts 1 / EJB 2 déployée sur WebSphere 6, sur Oracle 9i,
qui gère environ 38 000 plans PEA et PEA-PME pour la clientèle particuliers de la BAS. WebSphere 6
n'est plus supporté et le socle Java 1.4 bloque la mise en conformité PCI-DSS du parc.

La refonte a trois objectifs :
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
- Confluence : http://confluence.bas.intra/display/SIGEP/Architecture
