import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {FacilitiesService} from "../../core/services/facilities.service";
import {Facility} from "../../core/models/Facility";

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
  //TODO edit datatype

  loading = true;
  facility: Facility;

  //TODO load this from api when implemented
  isUserAdmin : boolean = true;

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.facility = facility;
        this.loading = false;
        //this.mapAttributes();
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
