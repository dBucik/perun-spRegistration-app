import {Component, Input, OnInit} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";

@Component({
  selector: 'app-application-item-boolean',
  templateUrl: './application-item-boolean.component.html',
  styleUrls: ['./application-item-boolean.component.scss']
})
export class ApplicationItemBooleanComponent implements OnInit, RequestItem {

  constructor() { }

  @Input()
  applicationItem: ApplicationItem;

  value: boolean = false;

  ngOnInit() {
  }

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.value);
  }
}
