import {Component, Input, ViewChild} from '@angular/core';
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {RequestItem} from "../../RequestItem";
import {Attribute} from "../../../../core/models/Attribute";
import {faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-application-item-string',
  templateUrl: './application-item-string.component.html',
  styleUrls: ['./application-item-string.component.scss']
})
export class ApplicationItemStringComponent implements RequestItem {

  constructor() { }

  helpIcon = faQuestionCircle;

  @Input()
  applicationItem: ApplicationItem;

  value: string = "";

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
}
