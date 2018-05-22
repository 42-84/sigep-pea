// Interfaces alignees sur sigep-portefeuille (PositionDTO, ValorisationDTO, Mouvement, OrdreDTO)
// et sur l'entite OperationEspeces de sigep-commun.
import { Plan } from './plan';
import { Valeur } from './referentiel';

export interface Position {
  codeIsin: string;
  libelle: string;
  mnemonique: string;
  typeValeur: string;
  quantite: number;
  prixRevientUnitaire: number;
  prixRevientTotal: number;
  dernierCours: number;
  dateDernierCours: Date | string; // string dd/MM/yyyy cote API
  valorisation: number;
  plusValueLatente: number;
  plusValuePourcent: number;
  coursIndisponible: boolean;
}

export interface Valorisation {
  numeroPlan: string;
  dateValorisation: Date | string; // string dd/MM/yyyy cote API
  valorisationTitres: number;
  soldeEspeces: number;
  total: number;
  totalVersements: number;
  plusValueLatente: number;
  prixRevientTotal: number;
  nombreLignes: number;
  partielle: boolean;
  positions: Position[];
}

export interface Mouvement {
  id?: number;
  reference: string;
  plan: Plan;
  valeur: Valeur;
  sens: string; // 'ACHAT' | 'VENTE'
  quantite: number;
  coursExecution: number;
  montantBrut: number;
  frais: number;
  montantNet: number;
  dateMouvement: Date | string; // TIMESTAMP cote base, mais serialise dd/MM/yyyy (l'heure est perdue)
  dateReglement: Date | string;
  statut: string; // 'SAISI' | 'EXECUTE' | 'REGLE' | 'ANNULE'
  canal: string;
  motifAnnulation: string;
}

export interface OperationEspeces {
  id?: number;
  compte: any;
  type: string; // VERSEMENT, RETRAIT, ACHAT_TITRES, VENTE_TITRES, DIVIDENDE, FRAIS, REGULARISATION, PRELEVEMENTS_SOCIAUX
  montant: number;
  dateOperation: Date | string; // string dd/MM/yyyy cote API
  dateValeur: Date | string;
  libelle: string;
  reference: string;
  soldeApres: number;
}

// OrdreDTO
export interface Ordre {
  codeIsin: string;
  sens: string;
  quantite: number;
  totalite: boolean;
}

export interface HistoriqueValorisation {
  date: Date | string;
  valorisationTitres: number;
  soldeEspeces: number;
  total: number;
  totalVersements: number;
}
