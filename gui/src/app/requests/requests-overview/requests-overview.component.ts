import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Request } from "../../core/models/Request";
import { ColumnSortedEvent } from "../../core/services/sort.service";
import { RequestStatus } from "../../core/models/RequestStatus";
import { RequestAction } from "../../core/models/RequestAction";
import { RequestsService } from "../../core/services/requests.service";
import { Subscription } from "rxjs";

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

  private requestsSubscription: Subscription;

  tableName = "requestsMainOverview";

  ngOnInit() {
    this.requestsSubscription = this.requestsService.getAllRequests().subscribe(requests => {
      this.requests = requests;
    });
  }

  ngOnDestroy() {
    this.requestsSubscription.unsubscribe();
  }

  onSorted($event: ColumnSortedEvent) {
    if ($event.tableName != this.tableName) {
      return;
    }

    this.requests = this.requests.sort((r1, r2) => {
      if ($event.sortColumn === 'id') {
        if ($event.sortDirection === 'asc') {
          return r1.reqId - r2.reqId;
        } else {
          return r2.reqId - r1.reqId;
        }
      }
    });
  }
}
