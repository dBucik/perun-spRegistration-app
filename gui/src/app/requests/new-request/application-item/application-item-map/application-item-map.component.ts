import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../../core/models/ApplicationItem';
import {RequestItem} from '../../../../core/models/RequestItem';
import {Attribute} from '../../../../core/models/Attribute';
import {NgForm} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-application-item-map',
  templateUrl: './application-item-map.component.html',
  styleUrls: ['./application-item-map.component.scss']
})
export class ApplicationItemMapComponent implements RequestItem, OnInit {

  constructor() {
    this.keys = [];
    this.values = [];
    this.indexes = [];
  }

  keys: string[];
  values: string[];
  indexes: number[];

  private index = 0;

  noItemError = false;
  duplicitKeysError = false;
  noValueError = false;

  disableCustomKeys = false;

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form', {static: false})
  form: NgForm;

  removeValue(index: number) {
    this.values.splice(index, 1);
    this.keys.splice(index, 1);
    this.indexes.splice(index, 1);

    if (this.values.length === 0) {
      this.noItemError = true;
    }
  }

  addValue() {
    this.values.push('');
    this.keys.push('');
    this.indexes.push(this.index++);
    this.noItemError = false;
  }

  addValueNonEmpty(key: string, value: string) {
    this.values.push(value);
    this.keys.push(key);
    this.indexes.push(this.index++);
    this.noItemError = false;
  }

  getAttribute(): Attribute {
    const map = new Map();

    for (let i = 0; i < this.values.length; i++) {
      map.set(this.keys[i], this.values[i]);
    }

    const obj = Array.from(map.entries()).reduce((main, [key, value]) => ({...main, [key]: value}), {});

    return new Attribute(this.applicationItem.name, obj);
  }

  customTrackBy(index: number, obj: any): any {
    return index;
  }

  private allValuesAreFilled(): boolean {
    if (this.values.length === 0) {
      return false;
    }
    for (const value of this.values) {
      if (value === undefined || value === null || value.trim().length === 0) {
        return false;
      }
    }

    return true;
  }

  hasCorrectValue(): boolean {
    // reset errors
    this.duplicitKeysError = false;
    this.noValueError = false;

    // not required
    if (! this.applicationItem.required) {
      return true;
    }

    // required
    if (this.disableCustomKeys && !this.allValuesAreFilled()) {
      this.noValueError = true;
      return false;
    }

    const keysWithIndexes = new Map<string, number>();

    for (let i = 0; i < this.values.length; i++) {
      const keys = Array.from(keysWithIndexes.keys());
      const key = this.keys[i].trim();

      if (keys.includes(key)) {
        this.duplicitKeysError = true;
        this.showErredKey(keysWithIndexes.get(key));
        this.showErredKey(i);

        return false;
      }

      keysWithIndexes.set(key, i);
    }

    return true;
  }

  showErredKey(orderNumber: number) {
    const index = this.indexes[orderNumber];
    const input = this.form.form.controls['key-' + index];

    input.markAsTouched();
    input.setErrors({'incorrect': true});
  }

  ngOnInit(): void {
    if (this.applicationItem.oldValue != null) {
      const map: Map<string, string> = this.applicationItem.oldValue;
      for (const [key, value] of Object.entries(map)) {
        this.addValueNonEmpty(key, value);
      }
    }
    if (this.applicationItem.allowedKeys !== undefined && this.applicationItem.allowedKeys.length > 0) {
      this.disableCustomKeys = true;

      if (this.values.length === 0) {
        this.keys = this.applicationItem.allowedKeys;

        for (let i = 0; i < this.keys.length; i++) {
          this.values.push('');
          this.indexes.push(this.index++);
        }
        this.noItemError = false;
      }
    }
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue() && this.values.length === 0) {
      this.noItemError = true;
    }
  }

  hasPattern(appItem: ApplicationItem): boolean {
    return (appItem.regex && (appItem.regex !== 'URL'));
  }

}
