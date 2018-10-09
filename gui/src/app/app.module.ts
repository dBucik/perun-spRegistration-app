import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PerunHeaderComponent } from './shared/layout/perun-header/perun-header.component';
import { CoreModule } from "./core/core.module";
import { FacilitiesModule } from "./facilities/facilities.module";
import { MainMenuModule } from "./main-menu/main-menu.module";
import { PerunSidebarComponent } from './shared/layout/perun-sidebar/perun-sidebar.component';
import { HttpClientModule } from "@angular/common/http";
import { SharedModule } from "./shared/shared.module";
import { FontAwesomeModule } from "@fortawesome/angular-fontawesome";

@NgModule({
  declarations: [
    AppComponent,
    PerunHeaderComponent,
    PerunSidebarComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    CoreModule,
    FacilitiesModule,
    HttpClientModule,
    MainMenuModule,
    SharedModule,
    FontAwesomeModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
