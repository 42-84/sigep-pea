import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { PlanService } from '../../services/plan.service';
import { ErreurService } from '../../services/erreur.service';
import { Plan } from '../../modeles/plan';
import { OperationEspeces } from '../../modeles/portefeuille';

@Component({
  selector: 'sigep-operations-especes',
  templateUrl: './operations-especes.component.html'
})
export class OperationsEspecesComponent implements OnInit, OnDestroy {

  plan: Plan;
  operations: OperationEspeces[] = [];
  chargement = false;
  erreur: string;

  private abonnement: Subscription;

  constructor(private planService: PlanService, private erreurService: ErreurService) { }

  ngOnInit() {
    this.plan = this.planService.getPlanCourant();
    this.charger();
    this.abonnement = this.planService.planChange.subscribe(plan => {
      this.plan = plan;
      this.charger();
    });
  }

  ngOnDestroy() {
    if (this.abonnement) {
      this.abonnement.unsubscribe();
    }
  }

  charger(): void {
    this.operations = [];
    this.erreur = null;
    if (!this.plan) {
      return;
    }
    this.chargement = true;
    // pas de filtre de dates ici : "le client veut voir tout son compte" (CR atelier 03/2018)
    this.planService.operations(this.plan.numeroPlan).subscribe(
      operations => {
        this.operations = operations.reverse();
        this.chargement = false;
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.chargement = false;
      });
  }

  libelleType(type: string): string {
    switch (type) {
      case 'VERSEMENT': return 'Versement';
      case 'RETRAIT': return 'Retrait';
      case 'ACHAT_TITRES': return 'Achat titres';
      case 'VENTE_TITRES': return 'Vente titres';
      case 'DIVIDENDE': return 'Dividende';
      case 'FRAIS': return 'Frais';
      case 'REGULARISATION': return 'Régularisation';
      case 'PRELEVEMENTS_SOCIAUX': return 'Prélèvements sociaux';
      default: return type;
    }
  }
}
