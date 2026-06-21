import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NovaLozinkaComponent } from './nova-lozinka.component';

describe('NovaLozinkaComponent', () => {
  let component: NovaLozinkaComponent;
  let fixture: ComponentFixture<NovaLozinkaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovaLozinkaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NovaLozinkaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
