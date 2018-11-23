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
export class ApplicationItemComponent implements RequestItem {

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

  getAttribute(): Attribute {
    if (this.applicationItem.type === 'java.lang.String') {
      return this.stringItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.lang.Boolean') {
      return this.booleanItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues === null) {
      return this.listItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues !== null) {
      return this.selectItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.util.LinkedHashMap') {
      return this.mapItem.getAttribute();
    }

    return undefined;
  }

  hasCorrectValue(): boolean {

    if (this.applicationItem.type === 'java.lang.String') {
      return this.stringItem.hasCorrectValue();
    }
    if (this.applicationItem.type === 'java.lang.Boolean') {
      return this.booleanItem.hasCorrectValue();
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues === null) {
      return this.listItem.hasCorrectValue();
    }
    if (this.applicationItem.type === 'java.util.ArrayList' && this.applicationItem.allowedValues !== null) {
      return this.selectItem.hasCorrectValue();
    }
    if (this.applicationItem.type === 'java.util.LinkedHashMap') {
      return this.mapItem.hasCorrectValue();
    }

    return true;
  }
}
