import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formate un montant en euros a la francaise : 1 234,56 €
 * Ecrit a la main : toLocaleString('fr-FR') n'est pas fiable sous IE11 et Node 8 (pas d'ICU complet).
 */
@Pipe({
  name: 'montantEuro'
})
export class MontantEuroPipe implements PipeTransform {

  transform(valeur: any, decimales: number = 2, devise: string = '€'): string {
    if (valeur === null || valeur === undefined || valeur === '') {
      return '';
    }
    const nombre = Number(valeur);
    if (isNaN(nombre)) {
      return String(valeur);
    }
    const negatif = nombre < 0;
    const fixe = Math.abs(nombre).toFixed(decimales);
    const parties = fixe.split('.');
    // separateur de milliers : espace insecable
    const entier = parties[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
    let resultat = entier;
    if (decimales > 0) {
      resultat += ',' + parties[1];
    }
    if (negatif) {
      resultat = '-' + resultat;
    }
    return devise ? resultat + ' ' + devise : resultat;
  }
}
