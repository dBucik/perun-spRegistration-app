import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import {MainMenuComponent} from "./main-menu/main-menu.component";

const routes: Routes = [
  {
    path: '',
    component: MainMenuComponent,
  },
  {
    path: 'requests',
    loadChildren: './requests/requests.module#RequestsModule'
  },
  {
    path: 'facilities',
    loadChildren: './facilities/facilities.module#FacilitiesModule'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: PreloadAllModules
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
