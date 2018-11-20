import {Component, Input} from '@angular/core';
import {faMinus, faPlus, faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {Attribute} from "../../../../core/models/Attribute";
import {RequestItem} from "../../RequestItem";

@Component({
  selector: 'app-application-item-select',
  templateUrl: './application-item-select.component.html',
  styleUrls: ['./application-item-select.component.scss']
})
export class ApplicationItemSelectComponent implements RequestItem {

  constructor() { }

  removeIcon = faMinus;
  addIcon = faPlus;
  helpIcon = faQuestionCircle;

  values = [];

  @Input()
  applicationItem: ApplicationItem;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }
}
