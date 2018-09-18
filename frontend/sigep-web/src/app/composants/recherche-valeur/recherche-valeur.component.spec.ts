import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RechercheValeurComponent } from './recherche-valeur.component';

describe('RechercheValeurComponent', () => {
  let component: RechercheValeurComponent;
  let fixture: ComponentFixture<RechercheValeurComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RechercheValeurComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RechercheValeurComponent);
    component = fixture.componentInstance;
});
