import { TestBed } from '@angular/core/testing';

import { ObjekatService } from './objekat.service';

describe('ObjekatService', () => {
  let service: ObjekatService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ObjekatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
