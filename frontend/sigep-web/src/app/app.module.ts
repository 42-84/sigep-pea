import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AccueilComponent } from './composants/accueil/accueil.component';
import { PositionsComponent } from './composants/positions/positions.component';
import { MouvementsComponent } from './composants/mouvements/mouvements.component';
import { OperationsEspecesComponent } from './composants/operations-especes/operations-especes.component';
import { SaisieOrdreComponent } from './composants/saisie-ordre/saisie-ordre.component';
import { VersementRetraitComponent } from './composants/versement-retrait/versement-retrait.component';
import { RechercheValeurComponent } from './composants/recherche-valeur/recherche-valeur.component';
import { SelecteurUtilisateurComponent } from './composants/selecteur-utilisateur/selecteur-utilisateur.component';

import { MontantEuroPipe } from './pipes/montant-euro.pipe';
import { DateFrPipe } from './pipes/date-fr.pipe';

import { PlanService } from './services/plan.service';
import { PortefeuilleService } from './services/portefeuille.service';
import { ReferentielService } from './services/referentiel.service';
import { ErreurService } from './services/erreur.service';
import { UtilisateurService } from './services/utilisateur.service';
import { SmUserInterceptor } from './services/sm-user.interceptor';

// locale fr pour les pipes Angular (number, percent) ; les montants passent par montantEuro
registerLocaleData(localeFr);

@NgModule({
  declarations: [
    AppComponent,
    AccueilComponent,
    PositionsComponent,
    MouvementsComponent,
    OperationsEspecesComponent,
    SaisieOrdreComponent,
    VersementRetraitComponent,
    RechercheValeurComponent,
    SelecteurUtilisateurComponent,
    MontantEuroPipe,
    DateFrPipe
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    PlanService,
    PortefeuilleService,
    ReferentielService,
    ErreurService,
    UtilisateurService,
    { provide: LOCALE_ID, useValue: 'fr-FR' },
    { provide: HTTP_INTERCEPTORS, useClass: SmUserInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
