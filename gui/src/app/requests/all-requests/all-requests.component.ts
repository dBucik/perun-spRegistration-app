import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {RequestsService} from "../../core/services/requests.service";
import {Request} from "../../core/models/Request";
import {MatSort, MatTableDataSource} from "@angular/material";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-all-requests',
  templateUrl: './all-requests.component.html',
  styleUrls: ['./all-requests.component.scss']
})
export class AllRequestsComponent implements OnInit {

  constructor(private requestsService: RequestsService) { }

  @Input()
  requests: Request[];


  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSource();
  }

  private requestsSubscription: Subscription;
  private sort: MatSort;

  loading = true;

  displayedColumns: string[] = ['reqId', 'reqUserId', 'facilityId', 'status', 'action'];
  dataSource: MatTableDataSource<Request>;

  setDataSource() {
    if (!!this.dataSource) {
      this.dataSource.sort = this.sort;
    }
  }

  ngOnInit() {
    this.requestsSubscription = this.requestsService.getAllRequests().subscribe(requests => {
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
}
