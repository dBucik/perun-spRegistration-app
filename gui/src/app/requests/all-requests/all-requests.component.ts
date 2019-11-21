import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { RequestsService } from '../../core/services/requests.service';
import { Request } from '../../core/models/Request';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-all-requests',
  templateUrl: './all-requests.component.html',
  styleUrls: ['./all-requests.component.scss']
})
export class AllRequestsComponent implements OnInit, OnDestroy {

  constructor(private requestsService: RequestsService) {
    this.requests = [];
    this.dataSource = new MatTableDataSource<Request>([]);
  }

  @ViewChild(MatPaginator, {static: false}) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSource();
  }
  @ViewChild(MatSort, {static: false}) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSource();
  }

  private paginator: MatPaginator = undefined;
  private sort: MatSort = undefined;

  private requestsSubscription: Subscription;
  requests: Request[];
  loading = true;
  displayedColumns: string[] = ['reqId', 'reqUserId', 'facilityId', 'status', 'action'];
  dataSource: MatTableDataSource<Request>;

  ngOnInit() {
    this.requestsSubscription = this.requestsService.getAllRequests().subscribe(requests => {
      this.loading = false;
      this.requests = requests;
      this.dataSource = new MatTableDataSource<Request>(requests);
    }, error => {
      this.loading = false;
    });
  }

  ngOnDestroy(): void {
    this.requestsSubscription.unsubscribe();
  }

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

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

}
