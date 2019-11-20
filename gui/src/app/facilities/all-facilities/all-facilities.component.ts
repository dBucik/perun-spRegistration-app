import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FacilitiesService} from '../../core/services/facilities.service';
import {Facility} from '../../core/models/Facility';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-all-facilities',
  templateUrl: './all-facilities.component.html',
  styleUrls: ['./all-facilities.component.scss']
})
export class AllFacilitiesComponent implements OnInit, OnDestroy {

  private sort: MatSort;
  private facilitiesSubscription: Subscription;

  constructor(private facilitiesService: FacilitiesService) { }

  @Input()
  facilities: Facility[];

  @ViewChild(MatSort, {static: false}) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSource();
  }

  displayedColumns: string[] = ['id', 'name', 'description', 'environment', 'protocol'];
  dataSource: MatTableDataSource<Facility>;
  loading = true;


  setDataSource() {
    if (!!this.dataSource) {
      this.dataSource.sort = this.sort;
    }
  }

  ngOnInit() {
    this.facilitiesSubscription = this.facilitiesService.getAllFacilities().subscribe(facilities => {
      this.loading = false;
      this.facilities = facilities;
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
