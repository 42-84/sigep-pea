import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { PlanService } from '../../services/plan.service';
import { ErreurService } from '../../services/erreur.service';
import { Plan, SynthesePlan } from '../../modeles/plan';

@Component({
  selector: 'sigep-accueil',
  templateUrl: './accueil.component.html',
  styleUrls: ['./accueil.component.css']
})
export class AccueilComponent implements OnInit, OnDestroy {

  plan: Plan;
    });
  }

  ngOnDestroy() {
    if (this.abonnement) {
      this.abonnement.unsubscribe();
    }
  }

  charger(): void {
    this.synthese = null;
    this.erreur = null;
    if (!this.plan) {
      return;
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.chargement = false;
      });
  }

  /** Plus-value latente en % des versements (calcul front, le DTO ne le donne pas). */
  plusValuePourcent(): number {
    if (!this.synthese || !this.synthese.valorisation || !this.synthese.totalVersements) {
      return null;
    }
    return this.synthese.valorisation.plusValueLatente / this.synthese.totalVersements * 100;
  }
}
