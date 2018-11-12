import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { SelecteurUtilisateurComponent } from './selecteur-utilisateur.component';
import { UtilisateurService } from '../../services/utilisateur.service';

describe('SelecteurUtilisateurComponent', () => {
  let component: SelecteurUtilisateurComponent;
  let fixture: ComponentFixture<SelecteurUtilisateurComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule ],
      declarations: [ SelecteurUtilisateurComponent ],
      providers: [ UtilisateurService ]
    })
    .compileComponents();
  }));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
