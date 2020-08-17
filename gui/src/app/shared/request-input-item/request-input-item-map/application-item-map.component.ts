import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../core/models/ApplicationItem';
import {RequestItem} from '../../../core/models/RequestItem';
import {Attribute} from '../../../core/models/Attribute';
import {NgForm} from '@angular/forms';
import {hasOwnProperty} from 'tslint/lib/utils';

@Component({
  selector: 'request-application-item-map',
  templateUrl: './application-item-map.component.html',
  styleUrls: ['./application-item-map.component.scss']
})
export class ApplicationItemMapComponent implements RequestItem, OnInit {

  constructor() { }

  entries: Map<string, string> = new Map<string, string>();
  keys: string[] = [];
  values: string[] = [];
  indexes: number[] = [];

  private index = 0;
  allowCustomKeys = false;

  duplicateKeysError: boolean = false;
  missingValueError: boolean = false;
  expectedValueChangedError: boolean = false;
  regexMismatchError: boolean = false;

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form', {static: false})
  form: NgForm;

  ngOnInit(): void {
    if (this.applicationItem.oldValue != null) {
      const map: Map<string, string> = this.applicationItem.oldValue;
      for (const [key, value] of Object.entries(map)) {
        this.addValueNonEmpty(key, value);
      }
    }
    if (this.applicationItem.allowedKeys !== undefined &&
      this.applicationItem.allowedKeys !== null &&
      this.applicationItem.allowedKeys.length > 0)
    {
      this.allowCustomKeys = true;

      if (this.values.length === 0) {
        this.keys = this.applicationItem.allowedKeys;

        for (let i = 0; i < this.keys.length; i++) {
          this.values.push('');
          this.indexes.push(this.index++);
        }
      }
    }

    if (this.applicationItem.required && this.keys.length === 0) {
      this.addValue();
    }
  }

  removeValue(index: number) {
    this.values.splice(index, 1);
    this.keys.splice(index, 1);
    this.indexes.splice(index, 1);
  }

  addValue() {
    this.values.push('');
    this.keys.push('');
    this.indexes.push(this.index++);
  }

  addValueNonEmpty(key: string, value: string) {
    this.values.push(value);
    this.keys.push(key);
    this.indexes.push(this.index++);
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

  hasCorrectValue(): boolean {
    this.resetErrors();

    if (!ApplicationItemMapComponent.hasValues(this.values)) {
      if (this.applicationItem.required) {
        this.missingValueError = true;
        return false;
      }
    } else {
      const errKeys = ApplicationItemMapComponent.checkDuplicities(this.keys, this.values);
      if (errKeys.length !== 0) {
        this.form.form.setErrors({'incorrect' : true});
        this.showErredKeys(errKeys);
        this.duplicateKeysError = true;
        return false;
      }

      const errValues = ApplicationItemMapComponent.checkRegex(this.applicationItem, this.values);
      if (errValues.length !== 0) {
        this.showErredValues(errValues);
        this.form.form.setErrors({'incorrect' : true});
        this.regexMismatchError = true;
        return false;
      }

      if (!ApplicationItemMapComponent.requestedChangeHasBeenMade(this.applicationItem, this.indexes, this.keys,
        this.values)) {
        this.form.form.setErrors({'incorrect' : true});
        this.expectedValueChangedError = true;
        return false;
      }

      if (!this.allValuesAreFilled()) {
        this.form.form.setErrors({'incorrect' : true});
        this.missingValueError = true;
        return false;
      }
    }

    return true;
  }

  showErredValues(errIndexes: number[]) {
    for (let i = 0; i < errIndexes.length; i++) {
      const index = this.indexes[i];
      const input = this.form.form.controls['value-' + index];

      input.markAsTouched();
      input.setErrors({'incorrect': true});
    }
  }

  showErredKeys(errIndexes: number[]) {
    for (let i = 0; i < errIndexes.length; i++) {
      const index = this.indexes[i];
      const input = this.form.form.controls['key-' + index];

      input.markAsTouched();
      input.setErrors({'incorrect': true});
    }
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      this.form.form.controls[this.applicationItem.name].markAsTouched();
      this.form.form.controls[this.applicationItem.name].setErrors({'incorrect' : true});
      this.form.form.controls[this.applicationItem.name].updateValueAndValidity();
    }
  }

  hasInputRegex(appItem: ApplicationItem): boolean {
    return appItem.hasRegex();
  }

  hasError(): boolean {
    return this.missingValueError || this.duplicateKeysError || this.missingValueError || this.regexMismatchError;
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

  private resetErrors(): void {
    this.expectedValueChangedError = false;
    this.regexMismatchError = false;
    this.missingValueError = false;
    this.duplicateKeysError = false;
  }

  private static requestedChangeHasBeenMade(appItem: ApplicationItem, indexes: number[], keys: string[],
                                              values: string[]): boolean {
    if (appItem.hasComment()) {
      const map = appItem.oldValue;
      for (let i = 0; i < indexes.length; i++) {
        const key = keys[i];
        if (!hasOwnProperty(map, key)) {
          return true;
        } else if (map[key] !== values[i]) {
          return true;
        }
      }
      return false;
    }

    return true;
  }

  private static checkRegex(item: ApplicationItem, values: string[]): number[] {
    let indexes = [];
    if (item.hasRegex()) {
      const regex = new RegExp(item.regex);
      for (let i = 0; i < values.length; i++) {
        if (!regex.test(values[i])) {
          indexes.push(i);
        }
      }
    }

    return indexes
  }

  private static hasValues(values: string[]): boolean {
    return values !== undefined &&
      values !== null &&
      values.length > 0;
  }

  private static checkDuplicities(keys: string[], values: string[]): number[] {
    const keysWithIndexes = new Map<string, number>();
    let duplicities = [];
    for (let i = 0; i < values.length; i++) {
      const keySet = Array.from(keysWithIndexes.keys());
      const key = keys[i].trim();

      if (keySet.includes(key)) {
        duplicities.push(i);
      }

      keysWithIndexes.set(key, i);
    }

    return duplicities;
  }

}
