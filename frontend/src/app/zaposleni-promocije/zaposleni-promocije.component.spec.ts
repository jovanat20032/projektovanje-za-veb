import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZaposleniPromocijeComponent } from './zaposleni-promocije.component';

describe('ZaposleniPromocijeComponent', () => {
  let component: ZaposleniPromocijeComponent;
  let fixture: ComponentFixture<ZaposleniPromocijeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZaposleniPromocijeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZaposleniPromocijeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
