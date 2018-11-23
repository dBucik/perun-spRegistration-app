import {Component, Input, ViewChild} from '@angular/core';
import {faMinus, faPlus, faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {ApplicationItem} from "../../../../core/models/ApplicationItem";
import {Attribute} from "../../../../core/models/Attribute";
import {RequestItem} from "../../RequestItem";
import {NgForm} from "@angular/forms";

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

  @ViewChild('form')
  form : NgForm;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }

  hasCorrectValue(): boolean {
    if (!this.applicationItem.required) {
      return true;
    }

    return this.values.length > 0;
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      this.form.form.controls[this.applicationItem.name].markAsTouched();
      this.form.form.controls[this.applicationItem.name].setErrors({'incorrect' : true});
    }
  }
}
