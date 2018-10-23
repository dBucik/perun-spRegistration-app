import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FacilitiesOverviewComponent } from "./facilities-overview/facilities-overview.component";

const routes: Routes = [
  {
    path: '',
    component: FacilitiesOverviewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FacilitiesRoutingModule { }
