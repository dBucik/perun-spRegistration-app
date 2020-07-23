import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestItemInputSelectComponent } from './request-item-input-select.component';

describe('ApplicationItemSelectComponent', () => {
  let component: RequestItemInputSelectComponent;
  let fixture: ComponentFixture<RequestItemInputSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequestItemInputSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestItemInputSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
