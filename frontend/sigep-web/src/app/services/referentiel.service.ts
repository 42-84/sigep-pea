import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { ApiConfig } from './api-config';
import { Cours, Eligibilite, Valeur } from '../modeles/referentiel';

/** Acces a sigep-referentiel (/api/valeurs, /api/cours). */
@Injectable()
export class ReferentielService {

  constructor(private http: HttpClient) { }

  /** Recherche par libelle, mnemonique ou ISIN (parametre q). eligibles=true ne renvoie que les valeurs eligibles PEA. */
  rechercher(q: string, eligibles: boolean = false): Observable<Valeur[]> {
    let params = new HttpParams();
    if (q) {
      params = params.set('q', q);
    }
  }

  /** typePlan : 'PEA' ou 'PEA_PME' (defaut PEA cote back). */
  eligibilite(isin: string, typePlan: string): Observable<Eligibilite> {
    const params = new HttpParams().set('typePlan', typePlan || 'PEA');
    return this.http.get<Eligibilite>(ApiConfig.REFERENTIEL + '/valeurs/' + isin + '/eligibilite', { params: params });
  }

  dernierCours(isin: string): Observable<Cours> {
    return this.http.get<Cours>(ApiConfig.REFERENTIEL + '/cours/' + isin + '/dernier');
  }

  historiqueCours(isin: string, du?: string, au?: string): Observable<Cours[]> {
    }
    return this.http.get<Cours[]>(ApiConfig.REFERENTIEL + '/cours/' + isin, { params: params });
  }
}
