import {Component, DoCheck, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {RequestsService} from "../../core/services/requests.service";
import {Subscription} from "rxjs";
import {Request} from "../../core/models/Request";
import {NgModel} from "@angular/forms";
import {PerunAttribute} from "../../core/models/PerunAttribute";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import {TranslateService} from "@ngx-translate/core";
import {AppComponent} from "../../app.component";
import {RequestSignature} from "../../core/models/RequestSignature";
import {RequestDetailDialogComponent} from "./request-detail-dialog/request-detail-dialog.component";

export interface DialogData {
  isApprove: boolean;
  isSetWFC: boolean;
  parent: RequestDetailComponent,
}

@Component({
    selector: 'app-request-detail',
    templateUrl: './request-detail.component.html',
    styleUrls: ['./request-detail.component.scss']
})
export class RequestDetailComponent implements OnInit, DoCheck {

    constructor(
        public dialog: MatDialog,
        private route: ActivatedRoute,
        private requestsService: RequestsService,
        private snackBar: MatSnackBar,
        private translate: TranslateService,
        private router: Router
    ) {

    }

    private sub: Subscription;
    //TODO edit datatype
    requestItems: any[];
    displayedColumns: string[] = ['name', 'value'];

    loading = true;
    request: Request;
    signatures: RequestSignature[];
    columns: string[] = ['name', 'signedAt', 'approved'];
    expansionPanelDisabled: boolean = true;
    icon: boolean = true;

    @ViewChild('input', {static: false})
    inputField: NgModel;

    successApproveMessage: string;
    successRejectMessage: string;
    successSetWFCMessage: string;
    noCommentErrorMessage: string;

    isApplicationAdmin: boolean;

    private mapAttributes() {
        this.requestItems = [];
        for (let urn of Object.keys(this.request.attributes)) {
            let item = this.request.attributes[urn];
            this.requestItems.push({
              "urn": urn,
              "value": item.value,
              "oldValue": item.oldValue,
              "name": item.definition.displayName,
              "comment": item.comment,
              "description": item.definition.description,
            });
        }
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.requestsService.getRequest(params['id']).subscribe(request => {
                this.request = request;
                this.mapAttributes();
                this.requestsService.getSignatures(this.request.reqId).subscribe(signatures => {
                  this.signatures = signatures;
                  if(this.signatures.length != 0){
                    this.expansionPanelDisabled = false;
                  }
                  this.loading = false;
                });
            }, error => {
                this.loading = false;
                console.log(error);
            });
        });
        this.translate.get("REQUESTS.REQUEST_ERROR").subscribe(value => this.noCommentErrorMessage = value);
        this.translate.get("REQUESTS.REJECTED").subscribe(value => this.successRejectMessage = value);
        this.translate.get("REQUESTS.APPROVED").subscribe(value => this.successApproveMessage = value);
        this.translate.get("REQUESTS.SET_WFC").subscribe(value => this.successSetWFCMessage = value);
        this.isApplicationAdmin = AppComponent.isApplicationAdmin();
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }

    ngDoCheck(): void {
      if (this.request !== undefined && this.request !== null) {
        if (this.request.status !== 'APPROVED' && this.request.status !== 'REJECTED') {
          this.displayedColumns = ['name', 'value', 'comment'];
        } else {
          this.displayedColumns = ['name', 'value'];
        }
      }
    }

    openApproveDialog(): void {
      this.dialog.open(RequestDetailDialogComponent, {
        width: '400px',
        data: {isApprove: true, isSetWFC: false, parent: this}
      });
    }

    openRejectDialog(): void {
      this.dialog.open(RequestDetailDialogComponent, {
        width: '400px',
        data: {isApprove: false, isSetWFC: false, parent: this}
      });
    }

    openSetWFCDialog(): void {
      this.dialog.open(RequestDetailDialogComponent, {
        width: '400px',
        data: {isApprove: false, isSetWFC: true, parent: this}
      });
    }

    onLoading() : void {
      this.loading = true;
    }

    reject() {
      this.requestsService.rejectRequest(this.request.reqId).subscribe(bool => {
        this.loading = false;
        this.snackBar.open(this.successRejectMessage, null, {duration: 6000});
        this.router.navigate(['/auth/requests/allRequests']);
        }, error => {
        console.log("Error");
        console.log(error);
      });

    }

    approve() {
      this.requestsService.approveRequest(this.request.reqId).subscribe(bool => {
        this.loading = false;
        this.snackBar.open(this.successApproveMessage, null, {duration: 6000});
        this.router.navigate(['/auth/requests/allRequests']);
        }, error => {
        console.log("Error");
        console.log(error);
      });
    }

    requestChanges() {
      for (let item of this.requestItems) {
        this.request.attributes[item.urn].comment = item.comment;
      }
      let array: Array<PerunAttribute> = [];
      for (const [key, value] of Object.entries(this.request.attributes)) {
        if ((value.comment != "") && (value.comment != null)) {
          array.push(value)
        }
      }
      this.requestsService.askForChanges(this.request.reqId, array).subscribe(bool => {
        this.loading = false;
        this.snackBar.open(this.successSetWFCMessage, null, {duration: 6000});
        this.ngOnInit()
      }, error => {
        console.log("Error");
        console.log(error);
      });
    }

    isUndefined(value) {
      //TODO: extract to one common method, also used in facility-detail
      if (value === undefined || value === null) {
        return true;
      } else {
        if (value instanceof Array || value instanceof String) {
          return value.length === 0;
        } else if (value instanceof Object) {
          return value.constructor === Object && Object.entries(value).length === 0
        }

        return false;
      }
    }

    changeArrow(){
      this.icon = !this.icon;
    }
}
