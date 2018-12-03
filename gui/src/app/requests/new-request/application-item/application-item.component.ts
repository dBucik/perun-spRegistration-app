import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../core/models/ApplicationItem";
import {RequestItem} from "../RequestItem";
import {Attribute} from "../../../core/models/Attribute";
import {ApplicationItemStringComponent} from "./application-item-string/application-item-string.component";
import {ApplicationItemBooleanComponent} from "./application-item-boolean/application-item-boolean.component";
import {ApplicationItemListComponent} from "./application-item-list/application-item-list.component";
import {ApplicationItemMapComponent} from "./application-item-map/application-item-map.component";
import {ApplicationItemSelectComponent} from "./application-item-select/application-item-select.component";

@Component({
  selector: 'app-application-item',
  templateUrl: './application-item.component.html',
  styleUrls: ['./application-item.component.scss']
})
export class ApplicationItemComponent implements RequestItem, AfterViewInit {

  constructor() { }

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild(ApplicationItemStringComponent)
  stringItem: RequestItem;
  @ViewChild(ApplicationItemBooleanComponent)
  booleanItem: RequestItem;
  @ViewChild(ApplicationItemListComponent)
  listItem: RequestItem;
  @ViewChild(ApplicationItemMapComponent)
  mapItem: RequestItem;
  @ViewChild(ApplicationItemSelectComponent)
  selectItem: RequestItem;

  item : RequestItem;

  getAttribute(): Attribute {
    return this.item.getAttribute();
  }

  hasCorrectValue(): boolean {
    return this.item.hasCorrectValue();
  }

  ngAfterViewInit(): void {
    if (this.applicationItem.type === 'java.lang.String') {
      this.item = this.stringItem;
    }
    if (this.applicationItem.type === 'java.lang.Boolean') {
      this.item = this.booleanItem;
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues === null) {
      this.item = this.listItem;
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues !== null) {
      this.item = this.selectItem;
    }
    if (this.applicationItem.type === 'java.util.LinkedHashMap') {
      this.item = this.mapItem;
    }
  }

  onFormSubmitted(): void {
    this.item.onFormSubmitted();
  }
}
