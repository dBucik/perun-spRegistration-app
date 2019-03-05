import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FacilitiesRoutingModule } from './facilities-routing.module';
import { FacilitiesOverviewComponent } from "./facilities-overview/facilities-overview.component";
import { SharedModule } from "../shared/shared.module";
import { FacilitiesDetailComponent } from './facilities-detail/facilities-detail.component';

@NgModule({
  imports: [
    CommonModule,
    FacilitiesRoutingModule,
    SharedModule
  ],
  declarations: [FacilitiesOverviewComponent, FacilitiesDetailComponent]
})
export class FacilitiesModule { }
