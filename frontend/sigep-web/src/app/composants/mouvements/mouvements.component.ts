import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import * as moment from 'moment';

import { PlanService } from '../../services/plan.service';
import { PortefeuilleService } from '../../services/portefeuille.service';
import { ErreurService } from '../../services/erreur.service';
import { ApiConfig } from '../../services/api-config';
import { Plan } from '../../modeles/plan';
import { Mouvement } from '../../modeles/portefeuille';

@Component({
  selector: 'sigep-mouvements',
  templateUrl: './mouvements.component.html'
})
export class MouvementsComponent implements OnInit, OnDestroy {

  plan: Plan;
  mouvements: Mouvement[] = [];
  chargement = false;
  erreur: string;
  erreurSaisie: string;

  // filtre de dates saisi en dd/MM/yyyy (meme format que l'API : on passe la string telle quelle)
  du: string;
  au: string;

  private abonnement: Subscription;

  constructor(private planService: PlanService,
              private portefeuilleService: PortefeuilleService,
              private erreurService: ErreurService) { }

  ngOnInit() {
    // par defaut : les 12 derniers mois
    this.au = moment().format(ApiConfig.FORMAT_DATE_API);
    this.du = moment().subtract(1, 'year').format(ApiConfig.FORMAT_DATE_API);
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

  private dateValide(valeur: string): boolean {
    return !valeur || moment(valeur, ApiConfig.FORMAT_DATE_API, true).isValid();
  }

  charger(): void {
    this.mouvements = [];
    this.erreur = null;
    this.erreurSaisie = null;
    if (!this.plan) {
      return;
    }
    if (!this.dateValide(this.du) || !this.dateValide(this.au)) {
      this.erreurSaisie = 'Les dates doivent être saisies au format jj/mm/aaaa.';
      return;
    }
    this.chargement = true;
    this.portefeuilleService.mouvements(this.plan.numeroPlan, this.du, this.au).subscribe(
      mouvements => {
        // l'API renvoie par date croissante, on affiche le plus recent en premier
        this.mouvements = mouvements.reverse();
        this.chargement = false;
      },
      err => {
        this.erreur = this.erreurService.libelle(err);
        this.chargement = false;
      });
  }

  toutAfficher(): void {
    this.du = null;
    this.au = null;
    this.charger();
  }

  classeStatut(statut: string): string {
    switch (statut) {
      case 'REGLE': return 'label-success';
      case 'EXECUTE': return 'label-primary';
      case 'ANNULE': return 'label-danger';
      default: return 'label-default';
    }
  }
}
