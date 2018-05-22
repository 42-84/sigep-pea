import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { UtilisateurService } from './utilisateur.service';

/**
 * Ajoute l'en-tete X-SM-User sur tous les appels HTTP (accepte par SiteMinderFilter cote back
 * comme equivalent de SM_USER). En prod l'agent SiteMinder pose lui-meme SM_USER.
 */
@Injectable()
export class SmUserInterceptor implements HttpInterceptor {

  constructor(private utilisateurService: UtilisateurService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const identifiant = this.utilisateurService.identifiantCourant();
    if (identifiant) {
      req = req.clone({ setHeaders: { 'X-SM-User': identifiant } });
    }
    return next.handle(req);
  }
}
