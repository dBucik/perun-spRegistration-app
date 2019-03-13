import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {FacilitiesService} from "../../../core/services/facilities.service";
import {Subscription} from "rxjs";
import {Facility} from "../../../core/models/Facility";

@Component({
  selector: 'app-facility-move-to-production',
  templateUrl: './facility-move-to-production.component.html',
  styleUrls: ['./facility-move-to-production.component.scss']
})
export class FacilityMoveToProductionComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService
  ) { }

  private sub : Subscription;

  switcherForm: boolean;

  loading = true;
  facility: Facility;

  ngOnInit() {
    this.switcherForm = false;
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.facility = facility;
        this.loading = false;
      });
    });
  }

  moveToProduction() : void {

  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
