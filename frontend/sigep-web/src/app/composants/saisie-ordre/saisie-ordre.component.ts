import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { PlanService } from '../../services/plan.service';
import { PortefeuilleService } from '../../services/portefeuille.service';
import { ReferentielService } from '../../services/referentiel.service';
import { ErreurService } from '../../services/erreur.service';
import { Plan } from '../../modeles/plan';
import { Mouvement, Ordre, Position } from '../../modeles/portefeuille';
import { Cours, Eligibilite, Valeur } from '../../modeles/referentiel';

/**
 * Saisie d'un ordre d'achat ou de vente (formulaire template-driven).
 * Etapes : saisie -> confirmation -> termine.
 */
@Component({
  selector: 'sigep-saisie-ordre',
  templateUrl: './saisie-ordre.component.html'
})
export class SaisieOrdreComponent implements OnInit, OnDestroy {

  plan: Plan;

  ordre: Ordre = { codeIsin: null, sens: 'ACHAT', quantite: null, totalite: false };

  mouvement: Mouvement;
  envoiEnCours = false;
  erreur: string;

  private abonnement: Subscription;

  constructor(private planService: PlanService,
              private portefeuilleService: PortefeuilleService,
    this.plan = this.planService.getPlanCourant();
    this.abonnement = this.planService.planChange.subscribe(plan => {
      this.plan = plan;
      this.reinitialiser();
    });
  }

  reinitialiser(): void {
    this.etape = 'saisie';
    this.recherche = '';
    this.resultats = [];
    this.valeur = null;
    this.cours = null;
    this.eligibilite = null;
    this.positionDetenue = null;
    this.ordre = { codeIsin: null, sens: 'ACHAT', quantite: null, totalite: false };
    this.mouvement = null;
    this.erreur = null;
  }

  rechercherValeur(): void {
    this.erreur = null;
    if (!this.recherche || this.recherche.trim().length < 2) {
      this.resultats = [];
      return;
    }
    this.rechercheEnCours = true;
    // en achat on ne propose que les valeurs eligibles (le back filtre sur eligiblePea uniquement,
    // l'eligibilite PEA-PME est verifiee ensuite par /eligibilite)
    this.referentielService.rechercher(this.recherche.trim(), this.ordre.sens === 'ACHAT').subscribe(
    this.positionDetenue = null;
    this.erreur = null;

    this.referentielService.dernierCours(valeur.codeIsin).subscribe(
      cours => this.cours = cours,
      err => {
        // pas de cours : on laisse l'utilisateur continuer, le back refusera si necessaire
        this.cours = null;
      });

    this.referentielService.eligibilite(valeur.codeIsin, this.plan ? this.plan.type : 'PEA').subscribe(
      eligibilite => this.eligibilite = eligibilite,
      err => this.erreur = this.erreurService.libelle(err));

    if (this.plan) {
      this.portefeuilleService.positions(this.plan.numeroPlan).subscribe(positions => {
        const detenues = positions.filter(p => p.codeIsin === valeur.codeIsin);
        this.positionDetenue = detenues.length > 0 ? detenues[0] : null;
      }, err => { /* silencieux : information facultative */ });
    }
  }

  changerSens(): void {
    // la liste de resultats depend du sens (filtre eligibles) : on la vide
    this.resultats = [];
    if (this.ordre.sens === 'ACHAT' && this.eligibilite && !this.eligibilite.eligible) {
      return false;
    }
    this.etape = 'confirmation';
  }

  retourSaisie(): void {
    this.etape = 'saisie';
  }

  envoyer(): void {
    this.envoiEnCours = true;
    this.erreur = null;
    const ordre: Ordre = {
      codeIsin: this.ordre.codeIsin,
      sens: this.ordre.sens,
      quantite: this.ordre.totalite ? null : Number(this.ordre.quantite),
      totalite: this.ordre.sens === 'VENTE' && this.ordre.totalite
    };
    this.portefeuilleService.passerOrdre(this.plan.numeroPlan, ordre).subscribe(
        this.envoiEnCours = false;
      },
      err => {
}
