import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { PlanService } from '../../services/plan.service';
import { PortefeuilleService } from '../../services/portefeuille.service';
import { ErreurService } from '../../services/erreur.service';
import { Plan } from '../../modeles/plan';
import { Position } from '../../modeles/portefeuille';

@Component({
  selector: 'sigep-positions',
  templateUrl: './positions.component.html',
  styleUrls: ['./positions.component.css']
})
export class PositionsComponent implements OnInit, OnDestroy {

  plan: Plan;
  positions: Position[] = [];
  chargement = false;
  erreur: string;

  colonneTri = 'libelle';
  triCroissant = true;

  private abonnement: Subscription;

  constructor(private planService: PlanService,
              private portefeuilleService: PortefeuilleService,
              private erreurService: ErreurService) { }

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
    this.positions = [];
    this.erreur = null;
    if (!this.plan) {
      return;
    }
    this.chargement = true;
    this.portefeuilleService.positions(this.plan.numeroPlan).subscribe(
      positions => {
        this.positions = positions;
        this.trier(this.colonneTri, true);
        this.chargement = false;
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.chargement = false;
      });
  }

  /** Tri cote client : un clic sur la meme colonne inverse le sens. */
  trier(colonne: string, conserverSens: boolean = false): void {
    if (!conserverSens) {
      if (this.colonneTri === colonne) {
        this.triCroissant = !this.triCroissant;
      } else {
        this.colonneTri = colonne;
        this.triCroissant = true;
      }
    }
    const sens = this.triCroissant ? 1 : -1;
    this.positions.sort((a: any, b: any) => {
      const va = a[colonne];
      const vb = b[colonne];
      if (va === vb) {
        return 0;
      }
      if (va === null || va === undefined) {
        return 1;
      }
      if (vb === null || vb === undefined) {
        return -1;
      }
      if (typeof va === 'string') {
        return va.localeCompare(vb) * sens;
      }
      return (va < vb ? -1 : 1) * sens;
    });
  }

  totalValorisation(): number {
    let total = 0;
    for (const p of this.positions) {
      if (!p.coursIndisponible && p.valorisation) {
        total += p.valorisation;
      }
    }
    return total;
  }

  totalPlusValue(): number {
    let total = 0;
    for (const p of this.positions) {
      if (!p.coursIndisponible && p.plusValueLatente) {
        total += p.plusValueLatente;
      }
    }
    return total;
  }

  nbCoursIndisponibles(): number {
    return this.positions.filter(p => p.coursIndisponible).length;
  }
}
