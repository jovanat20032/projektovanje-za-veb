import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PretragaRezervacijaComponent } from './pretraga-rezervacija.component';

describe('PretragaRezervacijaComponent', () => {
  let component: PretragaRezervacijaComponent;
  let fixture: ComponentFixture<PretragaRezervacijaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PretragaRezervacijaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PretragaRezervacijaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
