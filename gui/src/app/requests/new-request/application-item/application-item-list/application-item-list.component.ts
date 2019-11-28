import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../../core/models/ApplicationItem';
import {faMinus, faPlus, faQuestionCircle} from '@fortawesome/free-solid-svg-icons';
import {RequestItem} from '../../../../core/models/RequestItem';
import {Attribute} from '../../../../core/models/Attribute';
import {NgForm} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-application-item-list',
  templateUrl: './application-item-list.component.html',
  styleUrls: ['./application-item-list.component.scss']
})
export class ApplicationItemListComponent implements RequestItem, OnInit {

  constructor(private translate: TranslateService) {
    this.values = [];
  }

  error = false;
  noItemError = false;
  translatedName: string;
  translatedDescription: string;

  values: string[];

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form', {static: false})
  form: NgForm;

  removeValue(index: number) {
    this.values.splice(index, 1);
    if (this.values.length === 0) {
      this.noItemError = true;
    }
  }

  addValue() {
    this.values.push('');
    this.noItemError = false;
  }

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }

  customTrackBy(index: number, obj: any): any {
    return index;
  }

  hasCorrectValue(): boolean {
    if (!this.applicationItem.required) {
      return true;
    } else if (this.values === undefined || this.values === null || this.values.length === 0) {
      return false;
    }

    for (let i = 0; i < this.values.length; i++) {
      if (this.values[i].trim().length === 0) {
        return false;
      }
    }

    return true;
  }

  ngOnInit(): void {
    const browserLang = this.translate.getDefaultLang();
    this.translatedDescription = this.applicationItem.description[browserLang];
    this.translatedName = this.applicationItem.displayName[browserLang];
    if (this.applicationItem.oldValue != null) {
      this.values = this.applicationItem.oldValue;
    }
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      if (this.applicationItem.required && this.values.length === 0) {
        this.noItemError = true;
      }

      for (let i = 0; i < this.values.length; i++) {
        const value = this.values[i];

        if (value.trim().length === 0) {
          this.form.form.controls['value-' + i].markAsTouched();
          this.form.form.controls['value-' + i].setErrors({'incorrect' : true});
          this.form.form.controls['value-' + i].updateValueAndValidity();
        }
      }
    }
  }
}
