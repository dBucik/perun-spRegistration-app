import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainMenuRoutingModule } from './main-menu-routing.module';
import { MainMenuComponent } from './main-menu.component';
import { SharedModule } from "../shared/shared.module";
import { MenuButtonComponent } from './menu-button/menu-button.component';
import { FontAwesomeModule } from "@fortawesome/angular-fontawesome";

@NgModule({
  imports: [
    CommonModule,
    MainMenuRoutingModule,
    SharedModule,
    FontAwesomeModule
  ],

  declarations: [MainMenuComponent, MenuButtonComponent]
})
export class MainMenuModule { }
