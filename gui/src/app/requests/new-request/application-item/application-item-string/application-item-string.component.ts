import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {NgForm} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-application-item-string',
  templateUrl: './application-item-string.component.html',
  styleUrls: ['./application-item-string.component.scss']
})
export class ApplicationItemStringComponent implements RequestItem, OnInit {

  constructor(private translate: TranslateService) { }

  helpIcon = faQuestionCircle;

  @Input()
  applicationItem: ApplicationItem;

  value: string = "";
  translatedName: string;
  translatedDescription: string;

  @ViewChild('form')
  form : NgForm;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.value);
  }

  hasCorrectValue(): boolean {
    if (!this.applicationItem.required) {
      return true;
    }

    return this.value.trim().length > 0;
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      this.form.form.controls[this.applicationItem.name].markAsTouched();
      this.form.form.controls[this.applicationItem.name].setErrors({'incorrect' : true});
    }
  }

  ngOnInit(): void {
    let browserLang = this.translate.getBrowserLang();
    //TODO remove
    browserLang = 'en';
    console.log(browserLang);
    this.translatedDescription = this.applicationItem.description[browserLang];
    this.translatedName = this.applicationItem.displayName[browserLang];
  }
}
