import {Component, Input, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {faMinus, faPlus, faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-application-item-map',
  templateUrl: './application-item-map.component.html',
  styleUrls: ['./application-item-map.component.scss']
})
export class ApplicationItemMapComponent implements RequestItem {

  constructor() { }

  removeIcon = faMinus;
  addIcon = faPlus;
  helpIcon = faQuestionCircle;

  keys : string[] = [];
  values : string[] = [];

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form')
  form : NgForm;

  removeValue(index : number) {
    this.values.splice(index, 1);
    this.keys.splice(index, 1);
  }

  addValue() {
    this.values.push("");
    this.keys.push("");
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
    if (!this.applicationItem.required) {
      return true;
    } else {
      if (this.values.length === 0) {
        return false;
      }
    }

    for (let i = 0; i < this.values.length; i++) {
      let value = this.values[i];
      let key = this.keys[i];

      if (value.trim().length === 0 || key.trim().length === 0) {
        return false;
      }
    }

    return true;
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      for (let i = 0; i < this.values.length; i++) {
        let value = this.values[i];
        let key = this.keys[i];

        if (value.trim().length === 0) {
          this.form.form.controls['value-' + i].markAsTouched();
          this.form.form.controls['value-' + i].setErrors({'incorrect' : true});
        }

        if (key.trim().length === 0) {
          this.form.form.controls['key-' + i].markAsTouched();
          this.form.form.controls['key-' + i].setErrors({'incorrect' : true});
        }
      }
    }
  }
}
