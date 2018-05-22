// ErreurDTO renvoye par GestionnaireErreurs (HTTP 422 regle metier, 404, 403, 409, 502, 503, 500)
export interface ErreurApi {
  code: string;
  message: string;
  horodatage: any; // string dd/MM/yyyy cote API (l'heure est perdue, voir ConfigurationJackson)
  chemin: string;
}
