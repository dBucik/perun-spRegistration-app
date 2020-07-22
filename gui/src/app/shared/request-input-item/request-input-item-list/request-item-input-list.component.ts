import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../core/models/ApplicationItem';
import {RequestItem} from '../../../core/models/RequestItem';
import {Attribute} from '../../../core/models/Attribute';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'request-item-input-map',
  templateUrl: './request-item-input-list.component.html',
  styleUrls: ['./request-item-input-list.component.scss']
})
export class RequestItemInputListComponent implements RequestItem, OnInit {

  constructor() { }

  values: string[] = [];
  error: boolean = false;
  missingValueError: boolean = false;
  expectedValueChangedError: boolean = false;
  regexMismatchError: boolean = false;

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form', {static: false})
  form: NgForm;

  ngOnInit(): void {
    if (this.applicationItem.oldValue != null) {
      this.values = this.applicationItem.oldValue;
    }

    if (this.applicationItem.required && this.values.length === 0) {
      this.addValue();
    }
  }

  removeValue(index: number) {
    this.values.splice(index, 1);
  }

  addValue() {
    this.values.push('');
  }

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }

  customTrackBy(index: number, obj: any): any {
    return index;
  }

  hasCorrectValue(): boolean {
    this.resetErrors();
    if (!RequestItemInputListComponent.hasValue(this.values)) {
      if (this.applicationItem.required) {
        this.form.form.setErrors({'incorrect' : true});
        this.missingValueError = true;
        return false;
      }
    } else {
      const errIndexes = RequestItemInputListComponent.checkRegex(this.applicationItem, this.values);
      if (errIndexes.length > 0) {
        this.form.form.setErrors({'incorrect' : true});
        this.showErredValues(errIndexes);
        this.regexMismatchError = true;
        return false;
      }

      if (!RequestItemInputListComponent.requestedChangeHasBeenMade(this.applicationItem, this.values)) {
        this.form.form.setErrors({'incorrect' : true});
        this.expectedValueChangedError = true;
        return false;
      }
    }

    return true;
  }

  showErredValues(errIndexes: number[]) {
    for (let i = 0; i < errIndexes.length; i++) {
      const input = this.form.form.controls['value-' + i];

      input.markAsTouched();
      input.setErrors({'incorrect': true});
    }
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      for (let i = 0; i < this.values.length; i++) {
        const value = this.values[i];

        if (value.trim().length === 0) {
          this.form.form.controls['value-' + i].markAsTouched();
          this.form.form.controls['value-' + i].setErrors({'incorrect' : true});
          this.form.form.controls['value-' + i].updateValueAndValidity();
        }
      }
    }
  }

  hasInputRegex(appItem: ApplicationItem): boolean {
    return appItem.hasRegex();
  }

  hasError(): boolean {
    return this.missingValueError || this.missingValueError || this.regexMismatchError;
  }

  private resetErrors(): void {
    this.expectedValueChangedError = false;
    this.regexMismatchError = false;
    this.missingValueError = false;
  }

  private static requestedChangeHasBeenMade(appItem: ApplicationItem, values: string[]): boolean {
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

  private static hasValue(values: string[]): boolean {
    return values !== undefined && values !== null && values.length > 0;
  }

  private static checkRegex(item: ApplicationItem, values: string[]): number[] {
    let indexes = [];
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
}
