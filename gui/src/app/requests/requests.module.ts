import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RequestsRoutingModule } from './requests-routing.module';
import { RequestsOverviewComponent } from "./requests-overview/requests-overview.component";
import {SharedModule} from "../shared/shared.module";
import { NewRequestComponent } from './new-request/new-request.component';

@NgModule({
  imports: [
    CommonModule,
    RequestsRoutingModule,
    SharedModule
  ],
  declarations: [RequestsOverviewComponent, NewRequestComponent]
})
export class RequestsModule { }
