import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ConfigService} from "../../core/services/config.service";
import {ApplicationItem} from "../../core/models/ApplicationItem";
import {RequestsService} from "../../core/services/requests.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, Router} from "@angular/router";
import {Request} from "../../core/models/Request";
import {ApplicationItemComponent} from "../new-request/application-item/application-item.component";
import {UrnValuePair} from "../../core/models/UrnValuePair";
import {PerunAttribute} from "../../core/models/PerunAttribute";

@Component({
  selector: 'app-request-edit',
  templateUrl: './request-edit.component.html',
  styleUrls: ['./request-edit.component.scss']
})
export class RequestEditComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private configService: ConfigService,
    private requestsService: RequestsService,
    private snackBar: MatSnackBar,
    private translate: TranslateService,
    private router: Router) {
  }

  @ViewChildren(ApplicationItemComponent)
  items: QueryList<ApplicationItemComponent>;

  private sub: any;
  loading = true;
  isCardBodyVisible = false;
  isFormVisible = false;
  snackBarDurationMs = 5000;

  showServiceMore: boolean = false;
  showOrganizationMore: boolean = false;
  showProtocolMore: boolean = false;
  showAccessControlMore: boolean = false;

  request: Request;

  errorText: string;
  successActionText: string;

  commentedServiceAttrs: ApplicationItem[] = [];
  commentedOrganizationAttrs: ApplicationItem[] = [];
  commentedProtocolAttrs: ApplicationItem[] = [];
  commentedAccessControlAttrs: ApplicationItem[] = [];

  serviceAttrs: ApplicationItem[] = [];
  organizationAttrs: ApplicationItem[] = [];
  protocolAttrs: ApplicationItem[] = [];
  accessControlAttrs: ApplicationItem[] = [];

  ngOnInit() {
    this.clearArrays();
    this.translate.get('REQUESTS.NEW_VALUES_ERROR_MESSAGE')
      .subscribe(value => this.errorText = value);
    this.translate.get('REQUESTS.SUCCESSFULLY_SUBMITTED')
      .subscribe(value => this.successActionText = value);
    this.getAttributes();
  }

  revealForm() {
    this.loading = false;
    this.isCardBodyVisible = true;
    this.isFormVisible = true;
  }

  submitRequest() {
    this.loading = true;

    let perunAttributes: UrnValuePair[] = [];

    this.items.forEach(i => {
      let attr = i.getAttribute();
      let perunAttr = new UrnValuePair(attr.value, attr.urn);
      perunAttributes.push(perunAttr);
    });

    this.requestsService.updateRequest(this.request.reqId, perunAttributes).subscribe(_ => {
      this.snackBar.open(this.successActionText, null, {duration: this.snackBarDurationMs});
      this.router.navigate(['/auth/requests/detail/' + this.request.reqId]);
    });
  }

  private getAttributes(): void {
    this.sub = this.route.params.subscribe(params => {
      this.requestsService.getRequest(params['id']).subscribe(request => {
        this.request = new Request(request);
        this.pushInputs();
        this.filterAndSortArrays();
        this.revealForm();
        this.loading = false;
      }, error => {
        this.loading = false;
        console.log(error);
      });
    });
  }

  private filterAndSortArrays() {
    this.commentedServiceAttrs = RequestEditComponent.filterAndSort(this.commentedServiceAttrs);
    this.commentedOrganizationAttrs = RequestEditComponent.filterAndSort(this.commentedOrganizationAttrs);
    this.commentedProtocolAttrs = RequestEditComponent.filterAndSort(this.commentedProtocolAttrs);
    this.commentedAccessControlAttrs = RequestEditComponent.filterAndSort(this.commentedAccessControlAttrs);

    this.serviceAttrs = RequestEditComponent.filterAndSort(this.serviceAttrs);
    this.organizationAttrs = RequestEditComponent.filterAndSort(this.organizationAttrs);
    this.protocolAttrs = RequestEditComponent.filterAndSort(this.protocolAttrs);
    this.accessControlAttrs = RequestEditComponent.filterAndSort(this.accessControlAttrs);
  }

  private pushInputs() {
    this.clearArrays();
    this.request.serviceAttrs().forEach((attr, _) => {
      RequestEditComponent.pushInput(attr, this.commentedServiceAttrs, this.serviceAttrs);
    });

    this.request.organizationAttrs().forEach((attr, _) => {
      RequestEditComponent.pushInput(attr, this.commentedOrganizationAttrs, this.organizationAttrs);
    });

    this.request.protocolAttrs().forEach((attr, _) => {
      RequestEditComponent.pushInput(attr, this.commentedProtocolAttrs, this.protocolAttrs);
    });

    this.request.accessControlAttrs().forEach((attr, _) => {
      RequestEditComponent.pushInput(attr, this.commentedAccessControlAttrs, this.accessControlAttrs);
    });
  }

  private static pushInput(attr: PerunAttribute, commentedDest: ApplicationItem[], regularDest: ApplicationItem[]) {
    attr.input.oldValue = attr.value;

    if (attr.comment) {
      attr.input.isEdit = true;
      attr.input.comment = attr.comment;
      commentedDest.push(attr.input);
    } else {
      regularDest.push(attr.input);
    }
  }

  clearArrays(): void {
    this.commentedServiceAttrs = [];
    this.commentedOrganizationAttrs = [];
    this.commentedProtocolAttrs = [];
    this.commentedAccessControlAttrs = [];

    this.serviceAttrs = [];
    this.organizationAttrs = [];
    this.protocolAttrs = [];
    this.accessControlAttrs = [];
  }

  private changeServiceShowMore(){
    this.showServiceMore = !this.showServiceMore;
    this.ngOnInit();
  }

  private changeOrganizationShowMore(){
    this.showOrganizationMore = !this.showOrganizationMore;
    this.ngOnInit();
  }

  private changeProtocolShowMore(){
    this.showProtocolMore = !this.showProtocolMore;
    this.ngOnInit();
  }

  private changeAccessShowMore(){
    this.showAccessControlMore = !this.showAccessControlMore;
    this.ngOnInit();
  }

  private static filterAndSort(items: ApplicationItem[]): ApplicationItem[] {
    items = this.filterItems(items);
    items = this.sortItems(items);
    return items;
  }

  private static filterItems(items: ApplicationItem[]): ApplicationItem[] {
    items.filter((item) => { return item.displayed });
    return items
  }

  private static sortItems(items: ApplicationItem[]): ApplicationItem[] {
    items.sort((a, b) => {
      return a.displayPosition - b.displayPosition;
    });

    return items;
  }

}
