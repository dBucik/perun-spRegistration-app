import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../core/models/ApplicationItem";
import {RequestItem} from "../RequestItem";
import {Attribute} from "../../../core/models/Attribute";
import {ApplicationItemStringComponent} from "./application-item-string/application-item-string.component";
import {ApplicationItemBooleanComponent} from "./application-item-boolean/application-item-boolean.component";
import {ApplicationItemListComponent} from "./application-item-list/application-item-list.component";

@Component({
  selector: 'app-application-item',
  templateUrl: './application-item.component.html',
  styleUrls: ['./application-item.component.scss']
})
export class ApplicationItemComponent implements RequestItem, OnInit, AfterViewInit {

  constructor() { }

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild(ApplicationItemStringComponent)
  stringItem: RequestItem;
  @ViewChild(ApplicationItemBooleanComponent)
  booleanItem: RequestItem;
  @ViewChild(ApplicationItemListComponent)
  listItem: RequestItem;

  getAttribute(): Attribute {
    if (this.applicationItem.type === 'java.lang.String') {
      return this.stringItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.lang.Boolean') {
      return this.booleanItem.getAttribute();
    }
    if (this.applicationItem.type === 'java.util.ArrayList') {
      return this.listItem.getAttribute();
    }

    return undefined;
  }

  ngOnInit(): void {
  }


  ngAfterViewInit(): void {

  }
}
