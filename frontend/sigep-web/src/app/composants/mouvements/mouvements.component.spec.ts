import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { MouvementsComponent } from './mouvements.component';

describe('MouvementsComponent', () => {
  let component: MouvementsComponent;
  let fixture: ComponentFixture<MouvementsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule ],
      declarations: [ MouvementsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MouvementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // TODO KO depuis la montee Angular 5 : "The pipe 'dateFr' could not be found" + No provider for PlanService
  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
