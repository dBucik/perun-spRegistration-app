import {Component, Input, OnInit} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../../../core/models/RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-application-item-boolean',
  templateUrl: './application-item-boolean.component.html',
  styleUrls: ['./application-item-boolean.component.scss']
})
export class ApplicationItemBooleanComponent implements OnInit, RequestItem {

  constructor(private translate: TranslateService) { }

  @Input()
  applicationItem: ApplicationItem;

  value: boolean = false;
  translatedName: string;
  translatedDescription: string;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.value);
  }

  hasCorrectValue(): boolean {
    return !((this.value == this.applicationItem.oldValue) && (this.applicationItem.comment != null));
  }

  onFormSubmitted(): void {
    // nothing to do
  }

  ngOnInit(): void {
    let browserLang = this.translate.getDefaultLang();
    this.translatedDescription = this.applicationItem.description[browserLang];
    this.translatedName = this.applicationItem.displayName[browserLang];
    this.value = this.applicationItem.oldValue
  }
}
