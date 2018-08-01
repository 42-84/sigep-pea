import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

import { environment } from '../../environments/environment';

const CLE_STOCKAGE = 'sigep.smUser';

/**
 * Identifiant de l'utilisateur courant (= valeur de SM_USER).
 * En dev on le choisit dans le selecteur de la barre du haut et on le garde en localStorage.
 * En prod le reverse proxy SiteMinder ecrase l'en-tete : la valeur locale n'a aucun effet.
 */
@Injectable()
export class UtilisateurService {

  // emis a chaque changement d'utilisateur (le AppComponent recharge les plans)
  changement = new Subject<string>();

  identifiantCourant(): string {
    let identifiant: string = null;
    try {
      identifiant = localStorage.getItem(CLE_STOCKAGE);
    } catch (e) {
      // IE11 en mode protege : localStorage indisponible, on retombe sur la valeur par defaut
    }
    return identifiant ? identifiant : environment.smUserDefaut;
  }

  changer(identifiant: string): void {
    try {
      localStorage.setItem(CLE_STOCKAGE, identifiant);
    } catch (e) {
      // idem : on ignore
    }
    this.changement.next(identifiant);
  }

  utilisateursDisponibles(): { identifiant: string, libelle: string }[] {
    return environment.utilisateursDev;
  }

  /** Un identifiant client fait 8 chiffres ; les conseillers commencent par U. */
  estClient(): boolean {
    return /^[0-9]{8}$/.test(this.identifiantCourant());
  }
}
