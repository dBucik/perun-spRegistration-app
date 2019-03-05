import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FacilitiesOverviewComponent } from "./facilities-overview/facilities-overview.component";
import {FacilitiesDetailComponent} from "./facilities-detail/facilities-detail.component";

const routes: Routes = [
  {
    path: 'myFacilities',
    component: FacilitiesOverviewComponent
  },
  {
    path: 'detail/:id',
    component: FacilitiesDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FacilitiesRoutingModule { }
