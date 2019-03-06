import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {FacilitiesService} from "../../core/services/facilities.service";
import {Facility} from "../../core/models/Facility";
import {MatTableDataSource} from "@angular/material";
import {PerunAttribute} from "../../core/models/PerunAttribute";

@Component({
  selector: 'app-facilities-detail',
  templateUrl: './facilities-detail.component.html',
  styleUrls: ['./facilities-detail.component.scss']
})
export class FacilitiesDetailComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService
  ) { }

  private sub : Subscription;

  displayedColumns: string[] = ['fullname', 'value'];
  facilityAttributes: PerunAttribute[];

  loading = true;
  facility: Facility;

  //TODO load this from api when implemented
  isUserAdmin : boolean = true;

  private mapAttributes() {
  this.facilityAttributes = [];
    for (let urn of Object.keys(this.facility.attrs)) {
      let item = this.facility.attrs[urn];
      this.facilityAttributes.push(new PerunAttribute(item.value, item.definition.displayName));
    }
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.facility = facility;
        this.mapAttributes();
        this.loading = false;
      }, error => {
        this.loading = false;
        console.log(error);
      });
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
