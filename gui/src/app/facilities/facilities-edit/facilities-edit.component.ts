import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ConfigService} from '../../core/services/config.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';
import {ApplicationItemComponent} from '../../requests/new-request/application-item/application-item.component';
import {ApplicationItem} from '../../core/models/ApplicationItem';
import {PerunAttribute} from '../../core/models/PerunAttribute';
import {FacilitiesService} from '../../core/services/facilities.service';
import {Facility} from '../../core/models/Facility';

@Component({
  selector: 'app-facilities-edit',
  templateUrl: './facilities-edit.component.html',
  styleUrls: ['./facilities-edit.component.scss']
})
export class FacilitiesEditComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private configService: ConfigService,
    private facilityService: FacilitiesService,
    private snackBar: MatSnackBar,
    private translate: TranslateService,
    private router: Router) {
  }

  @ViewChildren(ApplicationItemComponent)
  items: QueryList<ApplicationItemComponent>;

  private sub: any;
  isFormVisible = false;
  isCardBodyVisible = false;
  loading = true;
  snackBarDurationMs = 8000;

  facility: Facility;

  // translations
  errorText: string;
  successActionText: string;
  failedActionText: string;
  errorWronglyFilledItem: string;
  errorRequestAlreadyExists: string;

  applicationItems: ApplicationItem[];


  private static filterItems(items: ApplicationItem[]): ApplicationItem[] {

    const filteredItems: ApplicationItem[] = [];

    items.forEach(item => {
      if (item.displayed) {
        filteredItems.push(item);
      }
    });

    return filteredItems;
  }

  ngOnInit() {
    this.translate.get('FACILITIES.NEW_VALUES_ERROR_MESSAGE')
      .subscribe(value => this.errorText = value);
    this.translate.get('REQUESTS.COULD_NOT_SUBMIT_EMPTY')
      .subscribe(value => this.failedActionText = value);
    this.translate.get('REQUESTS.SUCCESSFULLY_SUBMITTED')
      .subscribe(value => this.successActionText = value);
    this.translate.get('FACILITIES.WRONGLY_FILLED_ITEM')
      .subscribe(value => this.errorWronglyFilledItem = value);
    this.translate.get('FACILITIES.ERROR_REQUEST_ALREADY_EXISTS')
      .subscribe(value => this.errorRequestAlreadyExists = value);
    this.getAttributes();
  }

  revealForm() {
    this.loading = false;
    this.isCardBodyVisible = true;
    this.isFormVisible = true;
  }

  onLoading() {
    this.loading = true;
    this.isCardBodyVisible = false;
  }

  submitRequest() {
    this.loading = true;
    if (this.facility.activeRequestId != null) {
      this.snackBar.open(this.errorRequestAlreadyExists, null, {duration: 6000});
      return;
    }

    const perunAttributes: PerunAttribute[] = [];

    // set to false when one attribute has wrong value
    let allGood = true;
    this.items.forEach(i => {
      const attr = i.getAttribute();
      const perunAttr = new PerunAttribute(attr.value, attr.urn);
      if (!i.hasCorrectValue()) {
        this.snackBar.open(this.errorWronglyFilledItem, null, {duration: 6000});
        allGood = false;
        this.loading = false;
        return;
      }
      perunAttributes.push(perunAttr);
    });

    if (!allGood) {
      return;
    }

    this.facilityService.changeFacility(this.facility.id, perunAttributes).subscribe(reqId => {
      if (reqId === null) {
        this.loading = false;
        this.snackBar.open(this.failedActionText, null, {duration: this.snackBarDurationMs});
        return;
      } else {
        this.loading = false;
        this.snackBar.open(this.successActionText, null, {duration: this.snackBarDurationMs});
        this.router.navigate(['/auth/requests/detail/' + reqId]);
      }
    });
  }

  private getAttributes(): void {
    this.sub = this.route.params.subscribe(params => {
      this.facilityService.getFacilityWithInputs(params['id']).subscribe(facility => {
        this.facility = facility;

        this.applicationItems = [];
        for (const [key, value] of Object.entries(this.facility.attrs)) {
          value.input.oldValue = value.value;
          value.input.comment = value.comment;
          this.applicationItems.push(value.input);
          value.input.isEdit = false;
        }

        this.applicationItems = FacilitiesEditComponent.filterItems(this.applicationItems);
        this.revealForm();

        this.loading = false;
      }, error => {
        this.loading = false;
        console.log(error);
      });
    });
  }
}

