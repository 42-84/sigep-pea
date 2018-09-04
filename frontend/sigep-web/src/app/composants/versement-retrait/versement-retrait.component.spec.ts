import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VersementRetraitComponent } from './versement-retrait.component';

describe('VersementRetraitComponent', () => {
  let component: VersementRetraitComponent;
  let fixture: ComponentFixture<VersementRetraitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VersementRetraitComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VersementRetraitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
});
