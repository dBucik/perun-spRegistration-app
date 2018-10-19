import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {RequestsOverviewComponent} from "./requests-overview/requests-overview.component";
import {NewRequestComponent} from "./new-request/new-request.component";

const routes: Routes = [
  {
    path: '',
    component: RequestsOverviewComponent
  },
  {
    path: 'new',
    component: NewRequestComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RequestsRoutingModule { }
