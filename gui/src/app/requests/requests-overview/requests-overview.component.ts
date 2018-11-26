import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { Request } from "../../core/models/Request";
import { ColumnSortedEvent } from "../../core/services/sort.service";
import { RequestStatus } from "../../core/models/RequestStatus";
import { RequestAction } from "../../core/models/RequestAction";
import { RequestsService } from "../../core/services/requests.service";
import { Subscription } from "rxjs";
import {MatSort, MatTableDataSource} from "@angular/material";

@Component({
  selector: 'app-requests-overview',
  templateUrl: './requests-overview.component.html',
  styleUrls: ['./requests-overview.component.scss']
})
export class RequestsOverviewComponent implements OnInit, OnDestroy {

  constructor(private requestsService: RequestsService) { }

  @Input()
  requests: Request[] = [
    {
      reqId: 1,
      facilityId: 2,
      status: RequestStatus.CANCELED,
      action: RequestAction.DELETE_FACILITY,
      reqUserId: 3,
      attributes: [],
      modifiedAt: '10:20 12-12-2018',
      modifiedBy: 3
    },
    {
      reqId: 2,
      facilityId: 2,
      status: RequestStatus.APPROVED,
      action: RequestAction.UPDATE_FACILITY,
      reqUserId: 3,
      attributes: [],
      modifiedAt: '10:20 12-12-2018',
      modifiedBy: 3
    },
    {
      reqId: 3,
      facilityId: 5,
      status: RequestStatus.REJECTED,
      action: RequestAction.DELETE_FACILITY,
      reqUserId: 3,
      attributes: [],
      modifiedAt: '10:20 12-12-2018',
      modifiedBy: 3
    }
  ];

  @ViewChild(MatSort) sort: MatSort;

  private requestsSubscription: Subscription;

  loading = true;

  displayedColumns: string[] = ['reqId', 'facilityId', 'status', 'action'];
  dataSource: MatTableDataSource<Request>;

  ngOnInit() {
    this.requestsSubscription = this.requestsService.getAllRequests().subscribe(requests => {
      this.requests = requests;
      this.dataSource = new MatTableDataSource<Request>(requests);
      this.dataSource.sort = this.sort;
      this.loading = false;
    });
  }

  ngOnDestroy() {
    this.requestsSubscription.unsubscribe();
  }
}
