import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZaposleniKalendarComponent } from './zaposleni-kalendar.component';

describe('ZaposleniKalendarComponent', () => {
  let component: ZaposleniKalendarComponent;
  let fixture: ComponentFixture<ZaposleniKalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZaposleniKalendarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZaposleniKalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
