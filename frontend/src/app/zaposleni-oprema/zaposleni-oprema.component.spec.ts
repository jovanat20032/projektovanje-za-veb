import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZaposleniOpremaComponent } from './zaposleni-oprema.component';

describe('ZaposleniOpremaComponent', () => {
  let component: ZaposleniOpremaComponent;
  let fixture: ComponentFixture<ZaposleniOpremaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZaposleniOpremaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZaposleniOpremaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
