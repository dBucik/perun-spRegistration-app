import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import {MainMenuComponent} from './main-menu/main-menu.component';
import {NotFoundPageComponent} from './shared/not-found-page/not-found-page.component';
import {NotAuthorizedPageComponent} from './shared/not-authorized-page/not-authorized-page.component';
import {LoginComponent} from './login/login.component';
import {DocumentSignComponent} from './document-sign/document-sign.component';
import {ToolsComponent} from './tools/tools.component';

const routes: Routes = [

  {
    path: '',
    component: LoginComponent,
  },
  {
    path: 'auth',
    component: MainMenuComponent,
  },
  {
    path: 'auth/requests',
    loadChildren: './requests/requests.module#RequestsModule'
  },
  {
    path: 'auth/facilities',
    loadChildren: './facilities/facilities.module#FacilitiesModule'
  },
  {
    path: 'auth/sign',
    component: DocumentSignComponent
  },
  {
    path: 'auth/tools',
    component: ToolsComponent
  },
  {
    path: 'notAuthorized',
    component: NotAuthorizedPageComponent
  },
  {
    path: '**',
    component: NotFoundPageComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: PreloadAllModules
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
