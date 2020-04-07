import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FacilitiesService} from '../../core/services/facilities.service';
import {Facility} from '../../core/models/Facility';
import {AppComponent} from '../../app.component';
import { MatDialog } from '@angular/material/dialog';
import {FacilitiesDetailDialogComponent} from './facilities-detail-dialog/facilities-detail-dialog.component';
import {RequestsService} from '../../core/services/requests.service';
import {Subscription} from 'rxjs';
import {PerunAttribute} from "../../core/models/PerunAttribute";
import {User} from "../../core/models/User";
import {FacilityDetailItem} from "../../core/models/items/FacilityDetailItem";
import {FacilityDetailUserItem} from "../../core/models/items/FacilityDetailUserItem";

export interface DialogData {
  parent: FacilitiesDetailComponent;
  facilityName: string;
}

@Component({
  selector: 'app-facilities-detail',
  templateUrl: './facilities-detail.component.html',
  styleUrls: ['./facilities-detail.component.scss']
})
export class FacilitiesDetailComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService,
    private requestsService: RequestsService,
    private router: Router,
    public dialog: MatDialog,
  ) { }

  private sub: Subscription;

  displColumnsAttrs: string[] = ['fullname', 'value'];
  displColumnsAdmins: string[] = ['managerName', 'managerMail'];

  facilityAttrsService: FacilityDetailItem[] = [];
  facilityAttrsOrganization: FacilityDetailItem[] = [];
  facilityAttrsProtocol: FacilityDetailItem[] = [];
  facilityAttrsAccessControl: FacilityDetailItem[] = [];
  facilityAdmins: FacilityDetailUserItem[] = [];

  loading = true;
  facility: Facility;
  moveToProductionActive = false;

  running = 0;

  isUserAdmin: boolean;

  private mapAttributes() {
    this.facility.serviceAttrs().forEach((attr, urn) => {
      this.facilityAttrsService.push(new FacilityDetailItem(urn, attr));
    });

    this.facility.organizationAttrs().forEach((attr, urn) => {
      this.facilityAttrsOrganization.push(new FacilityDetailItem(urn, attr));
    });

    this.facility.protocolAttrs().forEach((attr, urn) => {
      this.facilityAttrsProtocol.push(new FacilityDetailItem(urn, attr));
    });

    this.facility.accessControlAttrs().forEach((attr, urn) => {
      this.facilityAttrsAccessControl.push(new FacilityDetailItem(urn, attr));
    });
  }

  private mapAdmins() {
    this.facilityAdmins = [];
    this.facility.admins.forEach(user => {
      this.facilityAdmins.push(new FacilityDetailUserItem(new User(user)));
    });
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.isUserAdmin = AppComponent.isApplicationAdmin();
      this.facilitiesService.getFacility(params['id']).subscribe((data) => {
        this.facility = new Facility(data);

        this.mapAttributes();
        this.mapAdmins();

        if (this.facility.activeRequestId) {
          console.log("here");
          this.loadMoveToProductionActive(this.facility.activeRequestId);
        }
        this.loading = false;
      }, error => {
        this.loading = false;
        console.log(error);
      });
    });

    this.isUserAdmin = AppComponent.isApplicationAdmin();
  }

  loadMoveToProductionActive(activeRequestId: number) {
    this.requestsService.getRequest(activeRequestId).subscribe(request => {
      this.moveToProductionActive = (request.action === 'MOVE_TO_PRODUCTION');
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  moveToProduction(): void {
    this.router.navigateByUrl('auth/facilities/moveToProduction/' + this.facility.id );
  }

  openDeleteDialog(): void {
    this.dialog.open(FacilitiesDetailDialogComponent, {
      width: '400px',
      data: {parent: this, facilityName: this.facility.name}
    });
  }

  deleteFacility(): void {
    this.facilitiesService.removeFacility(this.facility.id).subscribe(id => {
      this.loading = false;
      this.router.navigateByUrl('auth/requests/detail/' + id);
    });
  }

  addFacilityAdmin(): void {
    this.router.navigateByUrl('auth/facilities/addAdmin/' + this.facility.id );
  }

  regenerateClientSecret(): void {

    this.facilitiesService.regenerateClientSecret(this.facility.id).subscribe(next => {
    });
  }

  isUndefined(value) {
    // TODO: extract to one common method, also used in request-detail
    if (value === undefined || value === null) {
      return true;
    } else {
      if (value instanceof Array || value instanceof String) {
        return value.length === 0;
      } else if (value instanceof Object) {
        return value.constructor === Object && Object.entries(value).length === 0;
      }

      return false;
    }
  }
}
