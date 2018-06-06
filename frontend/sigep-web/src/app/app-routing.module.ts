import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AccueilComponent } from './composants/accueil/accueil.component';
import { PositionsComponent } from './composants/positions/positions.component';
import { MouvementsComponent } from './composants/mouvements/mouvements.component';
import { OperationsEspecesComponent } from './composants/operations-especes/operations-especes.component';
import { SaisieOrdreComponent } from './composants/saisie-ordre/saisie-ordre.component';
import { VersementRetraitComponent } from './composants/versement-retrait/versement-retrait.component';
import { RechercheValeurComponent } from './composants/recherche-valeur/recherche-valeur.component';

const routes: Routes = [
  { path: '', component: AccueilComponent },
  { path: 'positions', component: PositionsComponent },
  { path: 'mouvements', component: MouvementsComponent },
  { path: 'especes', component: OperationsEspecesComponent },
  { path: 'ordre', component: SaisieOrdreComponent },
  { path: 'versement-retrait', component: VersementRetraitComponent },
  { path: 'valeurs', component: RechercheValeurComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  // useHash : le reverse proxy ne sait pas rediriger les URL profondes vers index.html
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
