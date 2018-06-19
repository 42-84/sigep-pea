import { Component, OnInit } from '@angular/core';

import { UtilisateurService } from '../../services/utilisateur.service';

/**
 * Selecteur d'utilisateur de dev (simule SM_USER). Affiche dans la barre du haut.
 * Laisse en prod : le reverse proxy SiteMinder ecrase l'en-tete de toute facon.
 */
@Component({
  selector: 'sigep-selecteur-utilisateur',
  templateUrl: './selecteur-utilisateur.component.html',
  styleUrls: ['./selecteur-utilisateur.component.css']
})
export class SelecteurUtilisateurComponent implements OnInit {

  utilisateurs: { identifiant: string, libelle: string }[] = [];
  identifiant: string;

  constructor(private utilisateurService: UtilisateurService) { }

  ngOnInit() {
    this.utilisateurs = this.utilisateurService.utilisateursDisponibles();
    this.identifiant = this.utilisateurService.identifiantCourant();
  }

  changer(): void {
    this.utilisateurService.changer(this.identifiant);
  }
}
