import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {NgForm} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-application-item-map',
  templateUrl: './application-item-map.component.html',
  styleUrls: ['./application-item-map.component.scss']
})
export class ApplicationItemMapComponent implements RequestItem, OnInit {

  constructor(private translate: TranslateService) {
  }

  keys: string[] = [];
  values: string[] = [];
  indexes: number[] = [];

  private index = 0;

  noItemError = false;
  duplicitKeysError = false;

  translatedName: string;
  translatedDescription: string;

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form')
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
    this.values.push("");
    this.keys.push("");
    this.indexes.push(this.index++);
    this.noItemError = false;
  }

  getAttribute(): Attribute {
    let map = new Map();

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
    if (!this.applicationItem.required && this.values.length === 0) {
      return true;
    } else {
      if (this.values.length === 0) {
        return false;
      }
    }

    // reset error
    this.duplicitKeysError = false;

    let keysWithIndexes = new Map<string, number>();

    for (let i = 0; i < this.values.length; i++) {
      let keys = Array.from(keysWithIndexes.keys());

      let value = this.values[i].trim();
      let key = this.keys[i].trim();

      if (keys.includes(key)) {
        this.duplicitKeysError = true;
        this.showErredKey(keysWithIndexes.get(key));
        this.showErredKey(i);

        return false;
      }

      if (value.length === 0 || key.length === 0) {
        return false;
      }

      keysWithIndexes.set(key, i);
    }

    return true;
  }

  showErredKey(orderNumber: number) {
    let index = this.indexes[orderNumber];
    let input = this.form.form.controls['key-' + index];

    input.markAsTouched();
    input.setErrors({'incorrect': true});
  }

  showErredValue(orderNumber: number) {
    let index = this.indexes[orderNumber];
    let input = this.form.form.controls['value-' + index];

    input.markAsTouched();
    input.setErrors({'incorrect': true});
  }

  ngOnInit(): void {
    let browserLang = this.translate.getBrowserLang();
    //TODO remove
    browserLang = 'en';
    this.translatedDescription = this.applicationItem.description[browserLang];
    this.translatedName = this.applicationItem.displayName[browserLang];
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      if (this.values.length === 0) {
        this.noItemError = true;
      }
    }
  }
}
