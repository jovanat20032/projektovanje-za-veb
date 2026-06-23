import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProdavnicaComponent } from './prodavnica.component';

describe('ProdavnicaComponent', () => {
  let component: ProdavnicaComponent;
  let fixture: ComponentFixture<ProdavnicaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProdavnicaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProdavnicaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
