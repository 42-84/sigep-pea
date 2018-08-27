import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

/**
 * Affiche une date au format francais. Accepte un Date ou une string dd/MM/yyyy telle que
 * renvoyee par l'API (contournement : l'API renvoie la date en string, le DatePipe Angular
 * ne sait pas la parser et affiche "Invalid Date" sous IE11).
 */
@Pipe({
  name: 'dateFr'
})
export class DateFrPipe implements PipeTransform {

  transform(valeur: Date | string, format: string = 'DD/MM/YYYY'): string {
    if (!valeur) {
      return '';
    }
    let m: moment.Moment;
    if (typeof valeur === 'string') {
      // on essaie d'abord le format API, puis ISO (au cas ou)
      m = moment(valeur, 'DD/MM/YYYY', true);
      if (!m.isValid()) {
        m = moment(valeur);
      }
    } else {
      m = moment(valeur);
    }
    return m.isValid() ? m.format(format) : String(valeur);
  }
}
