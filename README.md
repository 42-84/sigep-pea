# SIGEP - Système d'Information de Gestion de l'Épargne en actions et des Plans

Banque de l'Aunis et de Saintonge - DSI / Pôle Épargne & Titres

SIGEP gère les Plans d'Épargne en Actions (PEA, loi du 16 juillet 1992) et les PEA-PME des clients
particuliers de la BAS : ouverture et cycle de vie des plans, comptes espèces associés, ordres
d'achat/vente, positions et valorisation, référentiel des valeurs éligibles et cours de clôture.
La version 2 (refonte 2016) remplace SIGEP v1 (Struts 1 / EJB 2 / WebSphere, 2004) par trois
services Spring Boot sur une base Oracle commune. La reprise des données de la v1 a eu lieu le
14/03/2016.

## Démarrage rapide

Prérequis : JDK 8 (Oracle, 8u92 ou supérieur), Gradle 3.5, Eclipse Mars avec Buildship. Le détail
du poste de développement est dans `docs/INSTALL.txt`.

```
gradle clean build
gradle :sigep-referentiel:bootRun
gradle :sigep-comptes:bootRun
gradle :sigep-portefeuille:bootRun
```

**Respecter l'ordre de démarrage** : `sigep-referentiel` (8081), puis `sigep-comptes` (8082), puis
`sigep-portefeuille` (8083). Les trois services partagent la même base H2 en développement
(`~/sigep-dev/sigep`) et chacun charge son `data.sql` au démarrage : si le portefeuille démarre
avant le référentiel, les insertions échouent sur les clés étrangères vers `SGP_VALEUR` et `SGP_PLAN`
(le service démarre quand même grâce à `continue-on-error`, mais la base est incomplète).

| Module              | Rôle                                                   | Port |
|---------------------|--------------------------------------------------------|------|
| sigep-commun        | entités JPA, repositories, DTO, sécurité SiteMinder    | -    |
| sigep-referentiel   | valeurs mobilières, cours, éligibilité PEA, import Fininfo | 8081 |
| sigep-comptes       | clients, plans, versements/retraits, comptes espèces, fiscalité | 8082 |
| sigep-portefeuille  | positions, ordres, valorisation, coupons               | 8083 |

Console H2 : http://localhost:8081/h2 (JDBC URL : `jdbc:h2:file:~/sigep-dev/sigep`, utilisateur `sigep`).

En développement l'en-tête `SM_USER` n'est pas posé par un reverse proxy : la propriété
`sigep.securite.utilisateur-dev=U030001` force un utilisateur back-office.

## Liens

- Intégration continue : http://jenkins.bas.intra/job/SIGEP/
- Qualité : http://sonar.bas.intra/dashboard?id=sigep
- Documentation projet : http://confluence.bas.intra/display/SIGEP
- Suivi des demandes : http://jira.bas.intra/browse/SIGEP
- Architecture : `docs/ARCHITECTURE.md` - Exploitation : `docs/EXPLOITATION.md`

## Contacts

- Architecte refonte : J. Martin (poste 4412)
- Développement : F. Dubois
- MOA Épargne & Titres : C. Lefranc
- DBA : dba@bas.intra
- Exploitation / astreinte : exploit-n2@bas.intra

---
Dernière mise à jour : 21/06/2017 (JM)

Ajout 14/05/2019 (FD) : le driver Oracle `ojdbc6.jar` n'est plus sur le Nexus interne suite à la
mise à jour de licence. Il faut le déposer à la main dans `lib/` (voir `lib/LISEZMOI.txt`). Sans ce
jar, seul le profil `dev` (H2) démarre.
