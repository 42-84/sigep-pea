import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PlanService } from './plan.service';
import { Plan } from '../modeles/plan';

describe('PlanService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PlanService]
    });
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));

  it('should be created', inject([PlanService], (service: PlanService) => {
    expect(service).toBeTruthy();
  }));

  it('appelle GET /api/clients/{numero}/plans et renvoie les plans',
    inject([PlanService, HttpTestingController], (service: PlanService, httpMock: HttpTestingController) => {
      const plansAttendus: any[] = [
        { numeroPlan: 'PEA000045127', type: 'PEA', statut: 'OUVERT', totalVersements: 87500, dateOuverture: '12/03/2009' }
      ];
      let resultat: Plan[];

      service.plansDuClient('00012345').subscribe(plans => resultat = plans);

      const req = httpMock.expectOne('/api/clients/00012345/plans');
      expect(req.request.method).toBe('GET');
      req.flush(plansAttendus);

      expect(resultat.length).toBe(1);
      expect(resultat[0].numeroPlan).toBe('PEA000045127');
      expect(resultat[0].dateOuverture).toBe('12/03/2009'); // string : pas de conversion cote service
    }));
});
