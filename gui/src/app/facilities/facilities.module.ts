import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FacilitiesRoutingModule } from './facilities-routing.module';
import { ServicesOverviewComponent } from './services-overview/services-overview.component';
import { SharedModule } from '../shared/shared.module';
import { FacilitiesDetailComponent } from './facilities-detail/facilities-detail.component';
import {FacilityAttributeValuePipe} from './facility-attribute-value.pipe';
import { FacilityMoveToProductionComponent } from './facilities-detail/facility-move-to-production/facility-move-to-production.component';
import { FacilitiesDetailDialogComponent } from './facilities-detail/facilities-detail-dialog/facilities-detail-dialog.component';
import { FacilityAddAdminComponent } from './facilities-detail/facility-add-admin/facility-add-admin.component';
import { FacilityAddAdminSignComponent } from './facilities-detail/facility-add-admin/facility-add-admin-sign/facility-add-admin-sign.component';
import { AllFacilitiesComponent } from './all-facilities/all-facilities.component';
import { FacilitiesEditComponent } from './facilities-edit/facilities-edit.component';
import {FacilityEnvironmentPipe} from './facility-environment.pipe';
import {MatTabsModule} from '@angular/material/tabs';
import {FacilityProtocolPipe} from './facility-protocol.pipe';
import {MatDialogModule} from '@angular/material/dialog';
import {MatPaginatorModule} from '@angular/material/paginator';

@NgModule({
  imports: [
    CommonModule,
    FacilitiesRoutingModule,
    SharedModule,
    MatTabsModule,
    MatDialogModule,
    MatPaginatorModule
  ],
  declarations: [
    ServicesOverviewComponent,
    FacilitiesDetailComponent,
    FacilityAttributeValuePipe,
    FacilityMoveToProductionComponent,
    FacilitiesEditComponent,
    FacilityMoveToProductionComponent,
    FacilitiesDetailDialogComponent,
    FacilityAddAdminComponent,
    FacilityAddAdminSignComponent,
    AllFacilitiesComponent,
    FacilityAttributeValuePipe,
    FacilityEnvironmentPipe,
    FacilityProtocolPipe
  ],
  entryComponents: [
    FacilitiesDetailDialogComponent,
  ]
})
export class FacilitiesModule { }
