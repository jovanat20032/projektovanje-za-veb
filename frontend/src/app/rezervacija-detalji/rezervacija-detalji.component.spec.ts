import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RezervacijaDetaljiComponent } from './rezervacija-detalji.component';

describe('RezervacijaDetaljiComponent', () => {
  let component: RezervacijaDetaljiComponent;
  let fixture: ComponentFixture<RezervacijaDetaljiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RezervacijaDetaljiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RezervacijaDetaljiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
