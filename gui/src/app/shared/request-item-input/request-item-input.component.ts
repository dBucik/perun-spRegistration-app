import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../core/models/ApplicationItem';
import {RequestItem} from '../../core/models/RequestItem';
import {Attribute} from '../../core/models/Attribute';
import {RequestInputItemStringComponent} from './request-item-input-string/request-input-item-string.component';
import {RequestItemInputBooleanComponent} from './request-item-input-boolean/request-item-input-boolean.component';
import {RequestItemInputListComponent} from './request-item-input-list/request-item-input-list.component';
import {RequestItemInputMapComponent} from './request-item-input-map/request-item-input-map.component';
import {RequestItemInputSelectComponent} from './request-item-input-select/request-item-input-select.component';

@Component({
  selector: 'request-input-item',
  templateUrl: './request-item-input.component.html',
  styleUrls: ['./request-item-input.component.scss']
})
export class RequestItemInputComponent implements RequestItem, AfterViewInit {

  constructor() { }

  @Input() applicationItem: ApplicationItem;
  @ViewChild(RequestInputItemStringComponent, {static: false}) stringItem: RequestItem;
  @ViewChild(RequestItemInputBooleanComponent, {static: false}) booleanItem: RequestItem;
  @ViewChild(RequestItemInputListComponent, {static: false}) listItem: RequestItem;
  @ViewChild(RequestItemInputMapComponent, {static: false}) mapItem: RequestItem;
  @ViewChild(RequestItemInputSelectComponent, {static: false}) selectItem: RequestItem;

  item: RequestItem;

  public static hasValueMultiValue(values: string[]): boolean {
    if (values === undefined || values === null || values.length === 0) {
      return false;
    }

    for (let i = 0; i < values.length; i++) {
      const subValue = values[i];
      if (subValue === undefined || subValue === null || subValue.length === 0) {
        return false;
      }
    }

    return true;
  }

  public static requestedChangeHasBeenMadeMultiValue(appItem: ApplicationItem, values: string[]): boolean {
    if (appItem.hasComment()) {
      if (appItem.oldValue.length !== values.length) {
        return true;
      }

      for (let i = 0; i < values.length; i++) {
        if (appItem.oldValue.indexOf(values[i]) === -1) {
          return true;
        }
      }

      return false;
    }

    return true;
  }

  public static checkRegexMultiValue(item: ApplicationItem, values: string[]): number[] {
    const indexes = [];
    if (item.hasRegex()) {
      const reg = new RegExp(item.regex);
      for (let i = 0; i < values.length; i++) {
        if (!reg.test(values[i])) {
          indexes.push(i);
        }
      }
    }

    return indexes;
  }

  getAttribute(): Attribute {
    return this.item.getAttribute();
  }

  hasCorrectValue(): boolean {
    return this.item.hasCorrectValue();
  }

  ngAfterViewInit(): void {
    if (this.applicationItem.type === 'java.lang.String' || this.applicationItem.type === 'java.lang.LargeString') {
      this.item = this.stringItem;
    } else if (this.applicationItem.type === 'java.lang.Boolean') {
      this.item = this.booleanItem;
    } else if ((this.applicationItem.type === 'java.util.ArrayList' || this.applicationItem.type === 'java.util.LargeArrayList')
        && this.applicationItem.allowedValues === null) {
      this.item = this.listItem;
    } else if ((this.applicationItem.type === 'java.util.ArrayList' || this.applicationItem.type === 'java.util.LargeArrayList')
        && !!this.applicationItem.allowedValues !== null) {
      this.item = this.selectItem;
    } else if (this.applicationItem.type === 'java.util.LinkedHashMap') {
      this.item = this.mapItem;
    } else {
      console.log('Did not find item', this.applicationItem);
    }
  }

  onFormSubmitted(): void {
    this.item.onFormSubmitted();
  }

}
