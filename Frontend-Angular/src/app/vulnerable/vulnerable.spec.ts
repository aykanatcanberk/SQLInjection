import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Vulnerable } from './vulnerable';

describe('Vulnerable', () => {
  let component: Vulnerable;
  let fixture: ComponentFixture<Vulnerable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Vulnerable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Vulnerable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
