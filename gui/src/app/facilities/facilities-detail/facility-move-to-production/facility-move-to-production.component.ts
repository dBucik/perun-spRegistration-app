import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FacilitiesService} from "../../../core/services/facilities.service";
import {Subscription} from "rxjs";
import {Facility} from "../../../core/models/Facility";
import {ConfigService} from "../../../core/services/config.service";
import {MatChipInputEvent} from "@angular/material";
import {COMMA, ENTER} from "@angular/cdk/keycodes";

@Component({
  selector: 'app-facility-move-to-production',
  templateUrl: './facility-move-to-production.component.html',
  styleUrls: ['./facility-move-to-production.component.scss']
})
export class FacilityMoveToProductionComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService,
    private configService: ConfigService
  ) { }

  private sub : Subscription;



  switcherForm: boolean;

  loading = true;
  facility: Facility;
  isEmailEnabled: boolean;
  emails: string[];

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  ngOnInit() {
    this.switcherForm = false;
      this.emails = [];
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.facility = facility;
        this.loading = false;
      });
    });
    this.configService.isAuthoritiesEnabled().subscribe(response =>{
        this.isEmailEnabled = response;
    });
  }

  moveToProduction() : void {
    this.facilitiesService.createRequest(this.facility.id, this.emails);
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    this.emails.push(value);

    if (input) {
        input.value = '';
    }
  }

 remove(email: string): void {
    const index = this.emails.indexOf(email);

    if (index >= 0) {
        this.emails.splice(index, 1);
    }
 }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
