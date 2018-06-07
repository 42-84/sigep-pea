# SIGEP - Référence des API REST (extrait)

Rédigé 09/2018 (FD) pour le prestataire du front. Swagger prévu (SIGEP-140), en attendant cette
page fait foi. Non exhaustif : les endpoints internes (appels de service à service) ne sont pas
tous listés.

Conventions :

- Tous les appels exigent l'en-tête `SM_USER` (posé par le reverse proxy en prod, à la main ou via
  `X-SM-User` en dev). Sans en-tête : 401. Utilisateur non habilité : 403.
- Les dates en paramètre de requête et dans les JSON sont au format `dd/MM/yyyy`.
- Les montants sont des nombres décimaux (2 décimales pour les euros, 4 pour les cours et le PRU,
  3 pour les quantités).
- Erreur métier : HTTP 422 avec un corps `{ "code", "message", "horodatage", "chemin" }`. Codes
  dans `docs/CODES_ERREUR.txt`. Ressource inconnue : 404. Service partenaire en erreur : 502 ;
  injoignable : 503.
- Par le reverse proxy, les URL sont préfixées par `/sigep` (ex. `https://sigep.bas.intra/sigep/api/plans/PEA000045127`).
  En direct sur les serveurs, pas de préfixe.

Profils : `CLIENT` < `CONSEILLER` < `BACK_OFFICE` < `ADMIN`. Un CLIENT ne voit que ses propres
plans et son propre dossier ; les autres profils voient tout.

## sigep-referentiel (8081)

| Méthode | URL | Profil min. | Description |
|---|---|---|---|
| GET | `/api/valeurs?q={texte}&eligibles={bool}` | CLIENT | Recherche de valeurs par libellé, ISIN ou mnémonique (non radiées). `eligibles=true` restreint aux valeurs éligibles PEA ou PEA-PME. |
| GET | `/api/valeurs/{isin}` | CLIENT | Détail d'une valeur (entité complète). |
| GET | `/api/valeurs/{isin}/eligibilite?typePlan={PEA\|PEA_PME}` | CLIENT | Éligibilité au type de plan : `{ codeIsin, typePlan, eligible, motif }`. |
| GET | `/api/cours/{isin}/dernier` | CLIENT | Dernier cours connu : `{ codeIsin, dateCours, cloture, ouverture, plusHaut, plusBas, volume, source }`. 404 si aucun cours. |
| GET | `/api/cours/{isin}/historique?du={date}&au={date}` | CLIENT | Historique des cours entre deux dates (défaut : un an glissant). |
| POST | `/api/cours/saisie` | BACK_OFFICE | Saisie manuelle d'un cours (corps `CoursDTO`). Écrase le cours du jour, marqué `SAISIE`. |
| POST | `/api/cours/import` | BACK_OFFICE | Import d'un fichier Fininfo (multipart, champ `fichier`). Renvoie les compteurs d'import. Réservé exploitation. |

## sigep-comptes (8082)

| Méthode | URL | Profil min. | Description |
|---|---|---|---|
| GET | `/api/clients?nom={nom}&prenom={prenom}` | CONSEILLER | Recherche de clients (nom obligatoire, commence par). |
| GET | `/api/clients/{numero}` | CLIENT (soi-même) | Dossier client (numéro RCC 8 chiffres). |
| GET | `/api/clients/{numero}/plans` | CLIENT (soi-même) | Plans du client, tous statuts. |
| POST | `/api/clients` | CONSEILLER | Création d'un client dans SIGEP (recopie du RCC). 201. |
| GET | `/api/plans/{numero}` | CLIENT (titulaire) | Détail d'un plan (`PEA` + 9 chiffres). |
| GET | `/api/plans/{numero}/synthese` | CLIENT (titulaire) | Synthèse pour l'écran d'accueil : plan, client, ancienneté, date des 5 ans, plafond, versements restants, solde espèces, valorisation (obtenue de sigep-portefeuille ; `valorisationIndisponible=true` si injoignable). |
| POST | `/api/plans` | CONSEILLER | Ouverture : `{ numeroClient, typePlan, codeAgence, versementInitial }`. 201. Crée le compte espèces et passe le versement initial. |
| POST | `/api/plans/{numero}/versements` | CLIENT (titulaire) | Versement : `{ montant, libelle }`. Contrôle du plafond. |
| GET | `/api/plans/{numero}/retraits/simulation?montant={m}` | CLIENT (titulaire) | Simulation : ancienneté, clôture entraînée ou non, gain proportionnel, taux et montant des prélèvements sociaux, net versé, avertissement. |
| POST | `/api/plans/{numero}/retrait` | CLIENT (titulaire) | Retrait : `{ montant, libelle, accepterCloture }`. Renvoie la simulation exécutée. `accepterCloture` obligatoire si le plan a moins de 5 ans. |
| GET | `/api/comptes-especes/{numero}/solde` | CLIENT (titulaire) | Solde : `{ numeroCompte, solde, devise, dateMaj }`. Numéro de compte sur 22 caractères (`17515` + agence + 9 chiffres + `00`). |

## sigep-portefeuille (8083)

| Méthode | URL | Profil min. | Description |
|---|---|---|---|
| GET | `/api/portefeuilles/{numero}/positions` | CLIENT (titulaire) | Lignes valorisées au dernier cours : quantité, PRU, valorisation, plus-value latente (montant et %), `coursIndisponible`. |
| GET | `/api/portefeuilles/{numero}/valorisation` | CLIENT (titulaire) | Valorisation globale : titres, espèces, total, versements, plus-value latente, `partielle`, liste des positions. |
| GET | `/api/portefeuilles/{numero}/mouvements?du&au` | CLIENT (titulaire) | Mouvements titres (achats, ventes, annulés). |
| POST | `/api/portefeuilles/{numero}/ordres` | CLIENT (titulaire) | Ordre : `{ codeIsin, sens: ACHAT\|VENTE, quantite, totalite }`. Exécuté immédiatement au dernier cours de clôture connu, frais selon barème. 201, renvoie le mouvement. |
| GET | `/api/portefeuilles/{numero}/historique?depuis={date}` | CLIENT (titulaire) | Photos quotidiennes de valorisation (défaut : un an) pour le graphique. |

## Actuator (sans `SM_USER`)

| URL | Description |
|---|---|
| `/health` | État du service et de la connexion base. Utilisé par Centreon et `deploy.sh`. |
| `/info` | Nom et version (`info.app.version`). |

---

À compléter : endpoints back-office (création/modification/radiation de valeur, clôture
administrative, annulation de mouvement 2.4.0), opérations du compte espèces, `GET /api/cours/etat`.
Voir le code des contrôleurs (`*Controleur.java`) en cas de doute, c'est lui qui
fait foi.
