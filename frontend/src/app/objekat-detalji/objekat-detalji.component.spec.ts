import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjekatDetaljiComponent } from './objekat-detalji.component';

describe('ObjekatDetaljiComponent', () => {
  let component: ObjekatDetaljiComponent;
  let fixture: ComponentFixture<ObjekatDetaljiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ObjekatDetaljiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ObjekatDetaljiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
