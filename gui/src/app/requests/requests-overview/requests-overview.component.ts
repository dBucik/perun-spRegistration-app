import { Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { Request } from '../../core/models/Request';
import { RequestsService } from '../../core/services/requests.service';
import { Subscription } from 'rxjs';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-requests-overview',
  templateUrl: './requests-overview.component.html',
  styleUrls: ['./requests-overview.component.scss']
})
export class RequestsOverviewComponent implements OnInit, OnDestroy {

  constructor(private requestsService: RequestsService) {
    this.requests = [];
    this.dataSource = new MatTableDataSource<Request>([]);
  }

  @Input()
  requests: Request[];

  @ViewChild(MatSort, {static: false}) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSource();
  }

  @ViewChild(MatPaginator, {static: false}) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSource();
  }

  private requestsSubscription: Subscription;
  private sort: MatSort;
  private paginator: MatPaginator;

  loading = true;

  displayedColumns: string[] = ['reqId', 'facilityId', 'status', 'action'];
  dataSource: MatTableDataSource<Request>;

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
    this.requestsSubscription = this.requestsService.getUserRequests().subscribe(requests => {
      this.loading = false;
      this.requests = requests;
      this.dataSource = new MatTableDataSource<Request>(requests);
    }, error => {
      this.loading = false;
      console.log(error);
    });
  }

  ngOnDestroy() {
    this.requestsSubscription.unsubscribe();
  }

  doFilter = (value: string) => {
    this.dataSource.filter = value.trim().toLocaleLowerCase();
  }
}
