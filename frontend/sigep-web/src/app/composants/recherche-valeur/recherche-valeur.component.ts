import { Component } from '@angular/core';

import { ReferentielService } from '../../services/referentiel.service';
import { ErreurService } from '../../services/erreur.service';
import { PlanService } from '../../services/plan.service';
import { Cours, Eligibilite, Valeur } from '../../modeles/referentiel';

/** Ecran de consultation du referentiel valeurs (lecture seule cote client). */
@Component({
  selector: 'sigep-recherche-valeur',
  templateUrl: './recherche-valeur.component.html'
})
export class RechercheValeurComponent {

  critere = '';
  eligiblesSeulement = false;
  resultats: Valeur[] = [];
  rechercheFaite = false;
  chargement = false;
  erreur: string;

  valeur: Valeur;
  cours: Cours;
  eligibilite: Eligibilite;

  constructor(private referentielService: ReferentielService,
              private planService: PlanService,
              private erreurService: ErreurService) { }

  rechercher(): void {
    this.erreur = null;
    this.valeur = null;
    if (!this.critere || this.critere.trim().length < 2) {
      this.erreur = 'Saisissez au moins 2 caractères.';
      return;
    }
    this.chargement = true;
    this.referentielService.rechercher(this.critere.trim(), this.eligiblesSeulement).subscribe(
      valeurs => {
        this.resultats = valeurs;
        this.rechercheFaite = true;
        this.chargement = false;
      },
    this.cours = null;
    this.eligibilite = null;
    const plan = this.planService.getPlanCourant();
    this.referentielService.dernierCours(valeur.codeIsin).subscribe(
      cours => this.cours = cours,
      err => this.cours = null);
    this.referentielService.eligibilite(valeur.codeIsin, plan ? plan.type : 'PEA').subscribe(
      e => this.eligibilite = e,
      err => this.eligibilite = null);
}
