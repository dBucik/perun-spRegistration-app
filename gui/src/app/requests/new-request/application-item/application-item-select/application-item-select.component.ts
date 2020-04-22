import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ApplicationItem} from '../../../../core/models/ApplicationItem';
import {Attribute} from '../../../../core/models/Attribute';
import {RequestItem} from '../../../../core/models/RequestItem';
import {NgForm} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-application-item-select',
  templateUrl: './application-item-select.component.html',
  styleUrls: ['./application-item-select.component.scss']
})
export class ApplicationItemSelectComponent implements RequestItem, OnInit {

  constructor(private translate: TranslateService) {
    this.values = [];
  }

  values: string[];
  translatedName: string;
  translatedDescription: string;

  @Input()
  applicationItem: ApplicationItem;

  @ViewChild('form', {static: false})
  form: NgForm;

  getAttribute(): Attribute {
    return new Attribute(this.applicationItem.name, this.values);
  }

  hasCorrectValue(): boolean {
    if (!this.applicationItem.required) {
      if (this.values === undefined || this.values === null) {
        this.values = [];
      }

      return true;
    }

    return this.values !== undefined && this.values !== null && this.values.length > 0;
  }

  onFormSubmitted(): void {
    if (!this.hasCorrectValue()) {
      this.form.form.controls[this.applicationItem.name].markAsTouched();
      this.form.form.controls[this.applicationItem.name].setErrors({'incorrect' : true});
    }
  }

  ngOnInit(): void {
    const browserLang = this.translate.getBrowserLang();
    this.translatedName = this.applicationItem.getLocalizedName(browserLang);
    this.translatedDescription = this.applicationItem.getLocalizedDescription(browserLang);

    this.values = this.applicationItem.oldValue;
  }
}
