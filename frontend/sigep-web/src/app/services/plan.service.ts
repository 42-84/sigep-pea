import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { ApiConfig } from './api-config';
import { DemandeMontant, Plan, SimulationRetrait, SynthesePlan } from '../modeles/plan';
import { OperationEspeces } from '../modeles/portefeuille';

/**
 * Acces a sigep-comptes (/api/clients, /api/plans) + porte le plan courant selectionne
 * dans la barre de navigation (partage entre tous les ecrans).
 */
@Injectable()
export class PlanService {

  private planCourant: Plan = null;

  // emis quand l'utilisateur change de plan (PEA <-> PEA-PME) ou d'utilisateur
  planChange = new Subject<Plan>();

  constructor(private http: HttpClient) { }

  selectionner(plan: Plan): void {
    this.planCourant = plan;
    this.planChange.next(plan);
  }

  synthese(numeroPlan: string): Observable<SynthesePlan> {
    return this.http.get<SynthesePlan>(ApiConfig.COMPTES + '/plans/' + numeroPlan + '/synthese');
  }

  verser(numeroPlan: string, demande: DemandeMontant): Observable<Plan> {
    return this.http.post<Plan>(ApiConfig.COMPTES + '/plans/' + numeroPlan + '/versements', demande);
  }

  simulerRetrait(numeroPlan: string, montant: number): Observable<SimulationRetrait> {
    const params = new HttpParams().set('montant', String(montant));
    return this.http.get<SimulationRetrait>(ApiConfig.COMPTES + '/plans/' + numeroPlan + '/retraits/simulation', { params: params });
  }

  retirer(numeroPlan: string, demande: DemandeMontant): Observable<SimulationRetrait> {
  operations(numeroPlan: string, du?: string, au?: string): Observable<OperationEspeces[]> {
    let params = new HttpParams();
    if (du) {
      params = params.set('du', du);
    }
    // TODO (prestataire 2018) : gerer la pagination, l'API renvoie tout l'historique
    return this.http.get<OperationEspeces[]>(ApiConfig.COMPTES + '/plans/' + numeroPlan + '/operations', { params: params });
  }
}
