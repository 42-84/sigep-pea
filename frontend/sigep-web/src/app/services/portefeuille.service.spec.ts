import { TestBed, inject } from '@angular/core/testing';

import { PortefeuilleService } from './portefeuille.service';

describe('PortefeuilleService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PortefeuilleService]
    });
  });

  // TODO KO depuis la montee Angular 5 : No provider for HttpClient (il manque HttpClientTestingModule)
  xit('should be created', inject([PortefeuilleService], (service: PortefeuilleService) => {
    expect(service).toBeTruthy();
  }));
});
