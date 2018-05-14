// Environnement de developpement : le front tourne sous ng serve (4200) et le proxy
// (proxy.conf.json) redirige /api/** vers les trois services Spring Boot.
// Le fichier environment.ts est remplace par environment.prod.ts avec `ng build --prod`.

export const environment = {
  production: false,
  // base des URL d'API : vide en dev (proxy webpack), '/sigep' en prod (reverse proxy)
  apiBase: '',
  // identifiant SM_USER simule en dev, envoye dans l'en-tete X-SM-User (voir SiteMinderFilter cote back)
  smUserDefaut: '00012345',
  utilisateursDev: [
    { identifiant: '00012345', libelle: 'Jean-Pierre GAUTREAU (client, PEA000045127)' },
    { identifiant: '00027811', libelle: 'Camille BERNARD (client, PEA-PME PEA000412077)' },
    { identifiant: 'U045123', libelle: 'Conseiller agence La Rochelle (U045123)' }
  ]
};
