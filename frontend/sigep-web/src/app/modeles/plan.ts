// Interfaces alignees sur les entites JPA de sigep-commun (Plan, Client, CompteEspeces)
// et les DTO de sigep-comptes. Les dates arrivent en string dd/MM/yyyy (ConfigurationJackson).

export interface Client {
  id?: number;
  numeroClient: string;
  civilite: string;
  nom: string;
  prenom: string;
  dateNaissance: Date | string; // string dd/MM/yyyy cote API
  adresseLigne1: string;
  adresseLigne2: string;
  codePostal: string;
  ville: string;
  codePays: string;
  id?: number;
  numeroCompte: string;
  solde: number;
  devise: string;
  version?: number;
}

export interface Plan {
  id?: number;
  numeroPlan: string;
  type: string; // 'PEA' | 'PEA_PME'
  client: Client;
  dateOuverture: Date | string; // string dd/MM/yyyy cote API
  datePremierVersement: Date | string;
  dateCloture: Date | string;
  statut: string; // 'OUVERT' | 'CLOTURE' | 'TRANSFERE' | 'SUSPENDU'
  totalVersements: number;
  compteEspeces: CompteEspeces;
  codeAgence: string;
  commentaire: string;
  // champs d'audit (EntiteAuditable), jamais affiches
  dateCreation?: any;
  dateMaj?: any;
  utilisateurMaj?: string;
}

export interface SynthesePlan {
  numeroPlan: string;
  typePlan: string;
  libelleType: string;
  statut: string;
  numeroClient: string;
  nomClient: string;
  dateCinqAns: Date | string;
  plafondVersements: number;
  totalVersements: number;
  versementsRestants: number;
  numeroCompteEspeces: string;
  soldeEspeces: number;
  valorisation: any; // ValorisationDTO, null si valorisationIndisponible
  valorisationIndisponible: boolean;
}

export interface SimulationRetrait {
}
