import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {RequestsOverviewComponent} from "./requests-overview/requests-overview.component";

const routes: Routes = [
  {
    path: '',
    component: RequestsOverviewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RequestsRoutingModule { }
