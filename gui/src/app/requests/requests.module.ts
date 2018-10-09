import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RequestsRoutingModule } from './requests-routing.module';
import { RequestsOverviewComponent } from "./requests-overview/requests-overview.component";
import {SharedModule} from "../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    RequestsRoutingModule,
    SharedModule
  ],
  declarations: [RequestsOverviewComponent]
})
export class RequestsModule { }
