import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

import { ApiConfig } from './api-config';
import { HistoriqueValorisation, Mouvement, Ordre, Position, Valorisation } from '../modeles/portefeuille';

/** Acces a sigep-portefeuille (/api/portefeuilles). */
@Injectable()
export class PortefeuilleService {

  constructor(private http: HttpClient) { }

  positions(numeroPlan: string): Observable<Position[]> {
    return this.http.get<Position[]>(ApiConfig.PORTEFEUILLE + '/portefeuilles/' + numeroPlan + '/positions')
      .pipe(
        // tri par defaut : libelle, le back renvoie dans l'ordre de la table
        map(positions => positions.sort((a, b) => (a.libelle || '').localeCompare(b.libelle || '')))
      );
  }

    return this.http.get<Mouvement[]>(ApiConfig.PORTEFEUILLE + '/portefeuilles/' + numeroPlan + '/mouvements', { params: params });
  }

  passerOrdre(numeroPlan: string, ordre: Ordre): Observable<Mouvement> {
    return this.http.post<Mouvement>(ApiConfig.PORTEFEUILLE + '/portefeuilles/' + numeroPlan + '/ordres', ordre);
  }

  annuler(numeroPlan: string, reference: string, motif?: string): Observable<Mouvement> {
    let params = new HttpParams();
    if (motif) {
      params = params.set('motif', motif);
    }
    return this.http.post<Mouvement>(
      ApiConfig.PORTEFEUILLE + '/portefeuilles/' + numeroPlan + '/mouvements/' + reference + '/annulation', null, { params: params });
  }

  /** Photos quotidiennes de valorisation (batch). depuis : string dd/MM/yyyy, defaut = 1 an cote back. */
  historique(numeroPlan: string, depuis?: string): Observable<HistoriqueValorisation[]> {
    let params = new HttpParams();
}
