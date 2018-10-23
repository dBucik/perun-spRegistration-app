import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Facility } from "../../core/models/Facility";
import { ColumnSortedEvent } from "../../core/services/sort.service";
import { FacilitiesService } from "../../core/services/facilities.service";
import { Subscription } from "rxjs";

@Component({
  selector: 'app-facilities-overview',
  templateUrl: './facilities-overview.component.html',
  styleUrls: ['./facilities-overview.component.scss']
})
export class FacilitiesOverviewComponent implements OnInit, OnDestroy {

  constructor(private facilitiesService: FacilitiesService) { }

  @Input()
  facilities: Facility[] = [
    {
      id: 1,
      name: "C Cloud META 9",
      description: "Very ugly and old facility",
      attrs: []
    },
    {
      id: 2,
      name: "A Cloud",
      description: "Very ugly and old facility and this is the only facility that uses the unordinary black papers for their customers. ",
      attrs: []
    },
    {
      id: 3,
      name: "X Cloud META",
      description: "Very ugly and old facility",
      attrs: []
    }
  ];

  tableName = "facilitiesMainOverview";

  private facilitiesSubscription: Subscription;

  ngOnInit() {
    this.facilitiesSubscription = this.facilitiesService.getMyFacilities().subscribe(facilities => {
      this.facilities = facilities;
    });
  }

  ngOnDestroy() {
    this.facilitiesSubscription.unsubscribe();
  }

  onSorted($event: ColumnSortedEvent) {
    if ($event.tableName != this.tableName) {
      return;
    }
    this.facilities = this.facilities.sort((f1, f2) => {
      if ($event.sortColumn === 'id') {
        if ($event.sortDirection === 'asc') {
          return f1.id - f2.id;
        } else {
          return f2.id - f1.id;
        }
      }
      if ($event.sortColumn === 'name') {
        if ($event.sortDirection === 'asc') {
          return f1.name > f2.name ? 1 : -1;
        } else {
          return f2.name > f1.name ? 1 : -1;
        }
      }
    });
  }
}
