import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { Facility } from '../../core/models/Facility';
import { FacilitiesService } from '../../core/services/facilities.service';
import { Subscription } from 'rxjs';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-facilities-overview',
  templateUrl: './services-overview.component.html',
  styleUrls: ['./services-overview.component.scss']
})
export class ServicesOverviewComponent implements OnInit, OnDestroy {

  constructor(private facilitiesService: FacilitiesService) {
    this.myFacilities = [];
    this.dataSource = new MatTableDataSource<Facility>([]);
  }

  @Input()
  myFacilities: Facility[];

  @ViewChild(MatSort, {static: false}) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSource();
  }

  @ViewChild(MatPaginator, {static: false}) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSource();
  }

  displayedColumns: string[] = ['id', 'name', 'description', 'environment', 'protocol'];
  dataSource: MatTableDataSource<Facility>;
  loading = true;

  private sort: MatSort;
  private paginator: MatPaginator;
  private facilitiesSubscription: Subscription;

  setDataSource() {
    if (!!this.dataSource) {
      if (!!this.sort) {
        this.dataSource.sort = this.sort;
      }
      if (!!this.paginator) {
        this.dataSource.paginator = this.paginator;
      }
    }
  }

  ngOnInit() {
    this.facilitiesSubscription = this.facilitiesService.getMyFacilities().subscribe(facilities => {
      this.loading = false;
      this.myFacilities = facilities;
      this.dataSource = new MatTableDataSource<Facility>(facilities);
    }, error => {
        this.loading = false;
        console.log(error);
    });
  }

  ngOnDestroy() {
    this.facilitiesSubscription.unsubscribe();
  }

  doFilter = (value: string) => {
    this.dataSource.filter = value.trim().toLocaleLowerCase();
  }
}
