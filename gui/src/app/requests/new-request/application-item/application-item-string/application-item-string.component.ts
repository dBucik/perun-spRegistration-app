import {Component, Input} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";

@Component({
  selector: 'app-application-item-string',
  templateUrl: './application-item-string.component.html',
  styleUrls: ['./application-item-string.component.scss']
})
export class ApplicationItemStringComponent implements RequestItem {

  constructor() { }

  @Input()
  applicationItem: ApplicationItem;

  value: string;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.value);
  }
}
