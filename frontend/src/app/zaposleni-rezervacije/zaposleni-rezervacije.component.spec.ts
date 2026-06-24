import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZaposleniRezervacijeComponent } from './zaposleni-rezervacije.component';

describe('ZaposleniRezervacijeComponent', () => {
  let component: ZaposleniRezervacijeComponent;
  let fixture: ComponentFixture<ZaposleniRezervacijeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZaposleniRezervacijeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZaposleniRezervacijeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
