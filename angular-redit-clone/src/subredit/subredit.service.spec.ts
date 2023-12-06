import { TestBed } from '@angular/core/testing';

import { SubreditService } from './subredit.service';

describe('SubreditService', () => {
  let service: SubreditService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubreditService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
