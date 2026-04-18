import { TestBed } from '@angular/core/testing';

import { LojaFormService } from './loja-form-service';

describe('LojaFormService', () => {
  let service: LojaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LojaFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
