import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { PlanService } from '../../services/plan.service';
import { ErreurService } from '../../services/erreur.service';
import { DemandeMontant, Plan, SimulationRetrait } from '../../modeles/plan';

@Component({
  selector: 'sigep-versement-retrait',
  templateUrl: './versement-retrait.component.html'
})
export class VersementRetraitComponent implements OnInit, OnDestroy {

  plan: Plan;
  onglet = 'versement'; // 'versement' | 'retrait'

  // versement
  versement: DemandeMontant = { montant: null, libelle: 'Versement' };
  versementEnCours = false;
  versementOk: Plan;

  // retrait
  montantRetrait: number;
  simulation: SimulationRetrait;
  accepterCloture = false;
  simulationEnCours = false;
  retraitEnCours = false;
  retraitOk: SimulationRetrait;

  erreur: string;

  private abonnement: Subscription;

  constructor(private planService: PlanService, private erreurService: ErreurService) { }

  ngOnInit() {
    this.plan = this.planService.getPlanCourant();
    this.abonnement = this.planService.planChange.subscribe(plan => {
      this.plan = plan;
      this.reinitialiser();
    });
  }

  ngOnDestroy() {
    if (this.abonnement) {
      this.abonnement.unsubscribe();
    }
  }

  changerOnglet(onglet: string): void {
    this.onglet = onglet;
    this.erreur = null;
  }

  reinitialiser(): void {
    this.versement = { montant: null, libelle: 'Versement' };
    this.versementOk = null;
    this.montantRetrait = null;
    this.simulation = null;
    this.accepterCloture = false;
    this.retraitOk = null;
    this.erreur = null;
  }

  // ---- versement ----

  verser(): void {
    if (!this.plan || !this.versement.montant || this.versement.montant <= 0) {
      return;
    }
    this.erreur = null;
    this.versementEnCours = true;
    const demande: DemandeMontant = { montant: Number(this.versement.montant), libelle: this.versement.libelle };
    this.planService.verser(this.plan.numeroPlan, demande).subscribe(
      plan => {
        this.versementOk = plan;
        this.versementEnCours = false;
        // le plan a change (totalVersements, solde) : on le propage aux autres ecrans
        this.planService.selectionner(plan);
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.versementEnCours = false;
      });
  }

  versementsRestants(): number {
    if (!this.plan) {
      return null;
    }
    // plafonds legaux 2018 (TypePlan cote back) - dupliques ici, a synchroniser si la loi change
    const plafond = this.plan.type === 'PEA_PME' ? 225000 : 150000;
    return plafond - this.plan.totalVersements;
  }

  // ---- retrait ----

  simuler(): void {
    if (!this.plan || !this.montantRetrait || this.montantRetrait <= 0) {
      return;
    }
    this.erreur = null;
    this.simulation = null;
    this.accepterCloture = false;
    this.retraitOk = null;
    this.simulationEnCours = true;
    this.planService.simulerRetrait(this.plan.numeroPlan, Number(this.montantRetrait)).subscribe(
      simulation => {
        this.simulation = simulation;
        this.simulationEnCours = false;
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.simulationEnCours = false;
      });
  }

  peutRetirer(): boolean {
    if (!this.simulation) {
      return false;
    }
    if (this.simulation.entraineCloture && !this.accepterCloture) {
      return false;
    }
    return true;
  }

  retirer(): void {
    if (!this.peutRetirer()) {
      return;
    }
    this.erreur = null;
    this.retraitEnCours = true;
    const demande: DemandeMontant = {
      montant: this.simulation.montantDemande,
      libelle: 'Retrait',
      accepterCloture: this.accepterCloture
    };
    this.planService.retirer(this.plan.numeroPlan, demande).subscribe(
      resultat => {
        this.retraitOk = resultat;
        this.simulation = null;
        this.retraitEnCours = false;
        // le solde / statut a change : on recharge le plan
        this.planService.detail(this.plan.numeroPlan).subscribe(plan => this.planService.selectionner(plan));
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.retraitEnCours = false;
      });
  }
}
