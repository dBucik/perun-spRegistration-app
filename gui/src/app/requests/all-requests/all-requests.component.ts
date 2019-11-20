import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {RequestsService} from '../../core/services/requests.service';
import {Request} from '../../core/models/Request';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-all-requests',
  templateUrl: './all-requests.component.html',
  styleUrls: ['./all-requests.component.scss']
})
export class AllRequestsComponent implements OnInit, OnDestroy, AfterViewInit {

  constructor(private requestsService: RequestsService) { }

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;

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
      this.setDataSource();
    }, error => {
      this.loading = false;
    });
  }

  ngOnDestroy(): void {
    this.requestsSubscription.unsubscribe();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  setDataSource() {
    if (!!this.dataSource) {
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    filterValue = filterValue.toLowerCase();
    this.dataSource.filter = filterValue;
  }

}
