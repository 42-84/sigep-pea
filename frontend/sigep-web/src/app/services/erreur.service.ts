import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ErreurApi } from '../modeles/erreur-api';

/**
 * Traduction des codes d'erreur metier (RegleMetierException) en libelles affichables.
 * Table incomplete : les codes non traduits affichent le message technique brut renvoye par l'API.
 * Liste des codes : docs/CODES_ERREUR.txt cote back (plus tenue a jour).
 */
const LIBELLES: { [code: string]: string } = {
  'ESPECES_INSUFFISANTS': 'Le solde du compte espèces est insuffisant pour cette opération.',
  'PLAFOND_DEPASSE': 'Ce versement dépasse le plafond légal de versements du plan.',
  'PLAN_NON_OUVERT': 'Le plan n\'est pas ouvert : opération impossible.',
  'VALEUR_NON_ELIGIBLE': 'Cette valeur n\'est pas éligible au plan sélectionné.',
  'QUANTITE_INVALIDE': 'La quantité saisie est invalide.',
  'QUANTITE_INSUFFISANTE': 'Vous ne détenez pas assez de titres pour cette vente.',
  'MONTANT_INVALIDE': 'Le montant saisi est invalide.',
  'CLOTURE_NON_ACCEPTEE': 'Ce retrait entraîne la clôture du plan : vous devez l\'accepter pour continuer.',
  'VERSEMENT_MINIMUM': 'Le montant du versement est inférieur au minimum autorisé.',
  'INDISPONIBLE': 'Service temporairement indisponible, merci de réessayer plus tard.',
  'ACCES_REFUSE': 'Vous n\'êtes pas habilité à effectuer cette opération.'
  // TODO (prestataire 2018) : completer avec les codes de sigep-referentiel (ISIN_INVALIDE, COURS_*...)
};

@Injectable()
export class ErreurService {

  /** Libelle FR a partir d'un code d'erreur metier. */
  traduire(code: string, messageBrut?: string): string {
    if (code && LIBELLES[code]) {
      return LIBELLES[code];
    }
    if (messageBrut) {
      return messageBrut;
    }
    return code ? 'Erreur ' + code : 'Erreur inconnue';
  }

  /** Libelle FR a partir de l'erreur HttpClient (err.error contient l'ErreurDTO si le back a repondu). */
  libelle(err: any): string {
    if (!err) {
      return 'Erreur inconnue';
    }
    if (err instanceof HttpErrorResponse || err.status !== undefined) {
      if (err.status === 0) {
        return 'Le serveur est injoignable.';
      }
      if (err.status === 401) {
        return 'Utilisateur non authentifié (en-tête SM_USER absent).';
      }
      const corps: ErreurApi = err.error;
      if (corps && corps.code) {
        return this.traduire(corps.code, corps.message);
      }
      return 'Erreur technique (HTTP ' + err.status + ').';
    }
    if (err.code) {
      return this.traduire(err.code, err.message);
    }
    return 'Erreur technique.';
  }
}
