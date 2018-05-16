import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { PlanService } from './services/plan.service';
import { UtilisateurService } from './services/utilisateur.service';
import { ErreurService } from './services/erreur.service';
import { Plan } from './modeles/plan';

@Component({
  selector: 'sigep-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  titre = 'SIGEP - Mon PEA';
  version = 'v2.7';

  plans: Plan[] = [];
  planCourant: Plan;
  numeroPlanCourant: string;
  chargement = false;
  erreur: string;

  constructor(private planService: PlanService,
              private utilisateurService: UtilisateurService,
              private erreurService: ErreurService,
              private router: Router) { }

  ngOnInit() {
    this.chargerPlans();
    // changement d'utilisateur dans le selecteur de dev : on recharge tout
    this.utilisateurService.changement.subscribe(() => {
      this.chargerPlans();
      this.router.navigate(['/']);
    });
    // un ecran a rafraichi le plan (versement, retrait) : on met a jour la liste
    this.planService.planChange.subscribe(plan => {
      if (plan && this.planCourant && plan.numeroPlan === this.planCourant.numeroPlan) {
        this.planCourant = plan;
        this.plans = this.plans.map(p => p.numeroPlan === plan.numeroPlan ? plan : p);
      }
    });
  }

  /**
   * Le numero client = SM_USER pour un profil CLIENT. On prend le premier plan OUVERT ;
   * s'il y en a plusieurs (PEA + PEA-PME) le select de la barre permet de changer.
   */
  chargerPlans(): void {
    const numeroClient = this.utilisateurService.identifiantCourant();
    this.chargement = true;
    this.erreur = null;
    this.planService.plansDuClient(numeroClient).subscribe(
      plans => {
        this.plans = plans.filter(p => p.statut === 'OUVERT');
        this.planCourant = this.plans.length > 0 ? this.plans[0] : null;
        this.numeroPlanCourant = this.planCourant ? this.planCourant.numeroPlan : null;
        this.planService.selectionner(this.planCourant);
        this.chargement = false;
      },
      err => {
        this.plans = [];
        this.planCourant = null;
        this.numeroPlanCourant = null;
        this.planService.selectionner(null);
        this.chargement = false;
        if (err.status === 404) {
          // conseiller ou identifiant inconnu : pas de plan rattache
          this.erreur = 'Aucun client ne correspond à l\'identifiant ' + numeroClient + '.';
        } else {
          this.erreur = this.erreurService.libelle(err);
        }
      });
  }

  changerPlan(): void {
    const trouves = this.plans.filter(p => p.numeroPlan === this.numeroPlanCourant);
    this.planCourant = trouves.length > 0 ? trouves[0] : null;
    this.planService.selectionner(this.planCourant);
  }

  libelleType(type: string): string {
    return type === 'PEA_PME' ? 'PEA-PME' : type;
  }
}
