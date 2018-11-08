import {Component, Input} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {faMinus, faPlus} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-application-item-map',
  templateUrl: './application-item-map.component.html',
  styleUrls: ['./application-item-map.component.scss']
})
export class ApplicationItemMapComponent implements RequestItem {

  constructor() { }

  removeIcon = faMinus;
  addIcon = faPlus;

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
    return undefined;
  }
}
