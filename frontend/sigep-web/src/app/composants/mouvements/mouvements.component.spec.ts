import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { MouvementsComponent } from './mouvements.component';

describe('MouvementsComponent', () => {
  let component: MouvementsComponent;
  let fixture: ComponentFixture<MouvementsComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MouvementsComponent);
    component = fixture.componentInstance;
  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
