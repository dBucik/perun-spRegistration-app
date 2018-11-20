import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationItemSelectComponent } from './application-item-select.component';

describe('ApplicationItemSelectComponent', () => {
  let component: ApplicationItemSelectComponent;
  let fixture: ComponentFixture<ApplicationItemSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationItemSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationItemSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
