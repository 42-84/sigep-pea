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

    if (!this.plan) {
      return;
    }
    this.chargement = true;
    this.portefeuilleService.positions(this.plan.numeroPlan).subscribe(
      positions => {
        this.positions = positions;
        this.trier(this.colonneTri, true);
        this.chargement = false;
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
}
