import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../../core/models/ApplicationItem';
import {RequestItem} from '../../../../core/models/RequestItem';
import {Attribute} from '../../../../core/models/Attribute';
import {NgForm, NgModel} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-application-item-string',
  templateUrl: './application-item-string.component.html',
  styleUrls: ['./application-item-string.component.scss']
})
export class ApplicationItemStringComponent implements RequestItem, OnInit {

  constructor() { }

  @Input()
  applicationItem: ApplicationItem;
  value = '';

  @ViewChild('form', {static: false})
  form: NgForm;

  @ViewChild('input', {static: false})
  inputField: NgModel;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.value);
  }

  hasCorrectValue(): boolean {
    if (!this.applicationItem.required) {
      return true;
    } else {
      if (this.value === undefined || this.value === null || this.value.trim().length === 0) {
        return false;
      }

      if ((this.value === this.applicationItem.oldValue) && ((this.applicationItem.comment != null))) {
        return false;
      }
    }

    return this.inputField.valid;
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      this.form.form.controls[this.applicationItem.name].markAsTouched();
      this.form.form.controls[this.applicationItem.name].setErrors({'incorrect' : true});
    }
  }

  ngOnInit(): void {
    this.value = this.applicationItem.oldValue;
  }

  hasPattern(appItem: ApplicationItem): boolean {
    return (appItem.regex && (appItem.regex !== 'URL'));
  }
}
