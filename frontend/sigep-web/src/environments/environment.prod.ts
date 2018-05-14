// Production : deploye sous /sigep derriere le reverse proxy SiteMinder, qui pose SM_USER.
// Le selecteur d'utilisateur de dev reste actif : le proxy ecrase l'en-tete de toute facon.

export const environment = {
  production: true,
  apiBase: '/sigep',
  smUserDefaut: '',
  utilisateursDev: [
    { identifiant: '00012345', libelle: 'Jean-Pierre GAUTREAU (client, PEA000045127)' },
    { identifiant: '00027811', libelle: 'Camille BERNARD (client, PEA-PME PEA000412077)' },
    { identifiant: 'U045123', libelle: 'Conseiller agence La Rochelle (U045123)' }
  ]
};
