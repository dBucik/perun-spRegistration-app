import {Component, Input} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {faMinus, faPlus, faQuestionCircle} from "@fortawesome/free-solid-svg-icons";

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
}
