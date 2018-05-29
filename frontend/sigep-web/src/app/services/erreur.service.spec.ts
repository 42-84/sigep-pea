import { TestBed, inject } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';

import { ErreurService } from './erreur.service';

describe('ErreurService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ErreurService]
    });
  });

  it('should be created', inject([ErreurService], (service: ErreurService) => {
    expect(service).toBeTruthy();
  }));

  it('traduit un code connu en libelle FR', inject([ErreurService], (service: ErreurService) => {
    expect(service.traduire('ESPECES_INSUFFISANTS', 'Solde 12.00 < 100.00'))
      .toBe('Le solde du compte espèces est insuffisant pour cette opération.');
  }));

  it('renvoie le message brut pour un code inconnu', inject([ErreurService], (service: ErreurService) => {
    expect(service.traduire('ISIN_INVALIDE', 'ISIN FR000 invalide (12 caracteres attendus)'))
      .toBe('ISIN FR000 invalide (12 caracteres attendus)');
  }));

  it('lit l ErreurDTO dans une HttpErrorResponse 422', inject([ErreurService], (service: ErreurService) => {
    const err = new HttpErrorResponse({
      status: 422,
});
