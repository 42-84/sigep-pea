import { environment } from '../../environments/environment';

/**
 * URL de base par service back-end. En dev les trois pointent sur la meme origine
 * (le proxy webpack route par prefixe) ; en prod tout passe par /sigep sur le reverse proxy.
 * On garde trois constantes au cas ou les services seraient un jour exposes separement.
 */
export class ApiConfig {
  static readonly REFERENTIEL = environment.apiBase + '/api';   // sigep-referentiel (8081) : /valeurs, /cours
  static readonly COMPTES = environment.apiBase + '/api';       // sigep-comptes (8082) : /clients, /plans, /comptes-especes
  static readonly PORTEFEUILLE = environment.apiBase + '/api';  // sigep-portefeuille (8083) : /portefeuilles

  // format des dates echangees avec l'API (DateUtils.FORMAT_JOUR cote Java)
  static readonly FORMAT_DATE_API = 'DD/MM/YYYY';
}
