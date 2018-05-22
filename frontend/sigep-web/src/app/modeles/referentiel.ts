// Interfaces alignees sur sigep-referentiel : l'API renvoie l'entite Valeur telle quelle (pas de DTO).

export interface Valeur {
  id?: number;
  codeIsin: string;
  mnemonique: string;
  libelle: string;
  typeValeur: string; // 'ACTION' | 'OPCVM' | 'ETF' | 'DROIT' | 'AUTRE'
  placeCotation: string;
  codePaysSiege: string;
  devise: string;
  eligiblePea: boolean;
  eligiblePeaPme: boolean;
  codeFininfo: string;
  dateRadiation: Date | string; // string dd/MM/yyyy cote API, null si non radiee
}

export interface Cours {
  codeIsin: string;
  dateCours: Date | string; // string dd/MM/yyyy cote API
  cloture: number;
  ouverture: number;
  plusHaut: number;
  motif: string;
}
