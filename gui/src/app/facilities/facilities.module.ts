import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FacilitiesRoutingModule } from './facilities-routing.module';
import { FacilitiesOverviewComponent } from "./facilities-overview/facilities-overview.component";
import { SharedModule } from "../shared/shared.module";
import { FacilitiesDetailComponent } from './facilities-detail/facilities-detail.component';
import {FacilityAttributeValuePipe} from "../facilities/facility-attribute-value.pipe";

@NgModule({
  imports: [
    CommonModule,
    FacilitiesRoutingModule,
    SharedModule
  ],
  declarations: [
    FacilitiesOverviewComponent,
    FacilitiesDetailComponent,
    FacilityAttributeValuePipe
  ]
})
export class FacilitiesModule { }
