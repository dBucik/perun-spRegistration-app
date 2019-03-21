import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FacilitiesService} from "../../../core/services/facilities.service";
import {Subscription} from "rxjs";
import {Facility} from "../../../core/models/Facility";
import {MatChipInputEvent, MatSnackBar, MatSnackBarConfig, MatSnackBarVerticalPosition} from "@angular/material";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {TranslateService} from "@ngx-translate/core";
import {ConfigService} from "../../../core/services/config.service";

@Component({
  selector: 'app-facility-move-to-production',
  templateUrl: './facility-move-to-production.component.html',
  styleUrls: ['./facility-move-to-production.component.scss']
})
export class FacilityMoveToProductionComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService,
    private snackBar: MatSnackBar,
    private translate: TranslateService,
    private configService: ConfigService
  ) { }

  private sub : Subscription;

  loading = true;

  facility: Facility;
  isEmailEnabled: boolean;
  switcherForm: boolean;
  emails: string[];

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  ngOnInit() {
    this.switcherForm = false;
    this.emails = [];
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.configService.isAuthoritiesEnabled().subscribe(response =>{
            this.isEmailEnabled = response;
            this.facility = facility;
            this.loading = false;
        });

      });
    });
  }

  moveToProduction() : void {
    this.facilitiesService.createRequest(this.facility.id, this.emails);
    this.openSnackBar('FACILITIES.MOVE_TO_PRODUCTION_SUCCESS');
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if(value == ""){
        return;
    }

    let EMAIL_REGEXP = /^[a-z0-9!#$%&'*+\/=?^_`{|}~.-]+@[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/i;
    if (value.length <= 5 || !EMAIL_REGEXP.test(value)) {
        this.openSnackBar('FACILITIES.MOVE_TO_PRODUCTION_EMAIL_ERROR');
    } else{
       this.emails.push(value);
       if (input) {
           input.value = '';
       }
    }
  }

 remove(email: string): void {
    const index = this.emails.indexOf(email);
    if (index >= 0) {
        this.emails.splice(index, 1);
    }
 }

 openSnackBar(message: string):void {
    let config = new MatSnackBarConfig();
    config.duration = 5000;
    this.translate.get(message).subscribe(result => {this.snackBar.open(result, null, config);});
 }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
