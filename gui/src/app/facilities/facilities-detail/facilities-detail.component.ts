import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FacilitiesService} from '../../core/services/facilities.service';
import {Facility} from '../../core/models/Facility';
import {AppComponent} from '../../app.component';
import {MatDialog} from '@angular/material';
import {FacilitiesDetailDialogComponent} from './facilities-detail-dialog/facilities-detail-dialog.component';
import {RequestsService} from '../../core/services/requests.service';
import {Subscription} from 'rxjs';

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
  facilityAttributes: any[];
  facilityAdmins: any[];

  loading = true;
  loadingProtocol = false;
  facility: Facility;
  protocolDetails: any[];
  moveToProductionActive = false;

  running = 0;

  isUserAdmin: boolean;

  private mapAttributes() {
    this.facilityAttributes = [];
      for (const urn of Object.keys(this.facility.attrs)) {
        const item = this.facility.attrs[urn];
        this.facilityAttributes.push(
          {
            'urn': urn,
            'value': item.value,
            'name': item.definition.displayName,
            'description': item.definition.description,
          }
        );
    }
  }

  private mapAdmins() {
    this.facilityAdmins = [];
    for (const user of this.facility.admins) {
      this.facilityAdmins.push(
        {
          'name': user.fullName,
          'email': user.email
        }
      );
    }
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.isUserAdmin = AppComponent.isApplicationAdmin();
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.startMethod();

        this.facility = facility;
        this.mapAttributes();
        this.mapAdmins();

        if (facility.protocol === 'OIDC') {
          this.loadOidcDetails(params['id']);
        } else if (facility.protocol === 'SAML') {
          this.loadSamlDetails(params['id']);
        }

        if (facility.activeRequestId) {
          this.loadMoveToProductionActive(facility.activeRequestId);
        }

        this.endMethod();
      }, error => {
        this.loading = false;
        console.log(error);
      });
    });

    this.isUserAdmin = AppComponent.isApplicationAdmin();
  }

  endMethod(): void {
    this.running--;
    if (this.running === 0) {
      this.loading = false;
    }
  }

  loadOidcDetails(id: number) {
    this.startLoadingProtocolDetails();
    this.facilitiesService.getOidcDetails(id).subscribe(oidcDetails => {
      this.protocolDetails = [];
      for (const urn of Object.keys(oidcDetails)) {
        const item = oidcDetails[urn];
        this.protocolDetails.push(
          {
            'urn': urn,
            'value': item.value,
            'name': item.definition.displayName,
            'description': item.definition.description,
          }
        );
      }

      this.endLoadingProtocolDetails();
    });
  }

  loadSamlDetails(id: number) {
    this.startLoadingProtocolDetails();
    this.facilitiesService.getSamlDetails(id).subscribe(samlDetails => {
      this.protocolDetails = [];
      for (const urn of Object.keys(samlDetails)) {
        const item = samlDetails[urn];
        this.protocolDetails.push(
          {
            'urn': urn,
            'value': item.value,
            'name': item.definition.displayName,
            'description': item.definition.description,
          }
        );
      }

      this.endLoadingProtocolDetails();
    });
  }

  loadMoveToProductionActive(activeRequestId: number) {
    this.startMethod();
    this.requestsService.getRequest(activeRequestId).subscribe(request => {
      this.moveToProductionActive = (request.action === 'MOVE_TO_PRODUCTION');

      this.endMethod();
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
    this.startLoadingProtocolDetails();

    this.facilitiesService.regenerateClientSecret(this.facility.id).subscribe(next => {
      this.loadOidcDetails(this.facility.id);
      this.endLoadingProtocolDetails();
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

  private startMethod() {
    this.loading = true;
    this.running++;
  }

  private startLoadingProtocolDetails() {
    this.loadingProtocol = true;
    this.running++;
  }

  private endLoadingProtocolDetails() {
    this.loadingProtocol = false;
    this.running--;
    if (this.running === 0) {
      this.loading = false;
    }
  }
}
