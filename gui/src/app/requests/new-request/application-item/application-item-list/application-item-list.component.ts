import {Component, Input} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {faMinus, faPlus, faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";

@Component({
  selector: 'app-application-item-list',
  templateUrl: './application-item-list.component.html',
  styleUrls: ['./application-item-list.component.scss']
})
export class ApplicationItemListComponent implements RequestItem {

  constructor() { }

  removeIcon = faMinus;
  addIcon = faPlus;
  helpIcon = faQuestionCircle;

  values : string[] = [];

  @Input()
  applicationItem: ApplicationItem;

  removeValue(index : number) {
    this.values.splice(index, 1);
  }

  addValue() {
    this.values.push("");
  }

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }

  customTrackBy(index: number, obj: any): any {
    return index;
  }
}
