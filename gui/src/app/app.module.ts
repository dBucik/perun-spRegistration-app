import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FacilitiesModule } from './facilities/facilities.module';
import { MainMenuModule } from './main-menu/main-menu.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { SharedModule } from './shared/shared.module';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login/login.component';
import { DocumentSignComponent } from './document-sign/document-sign.component';
import {PerunHeaderComponent} from './perun-header/perun-header.component';
import {PerunFooterCstComponent} from './perun-footer-cst/perun-footer-cst.component';
import {ToolsComponent} from './tools/tools.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatTabsModule} from '@angular/material/tabs';
import {AttributeValuePipe} from './attribute-value.pipe';
import {DocumentSignItemLocalePipe} from "./document-sign-item-locale.pipe";

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DocumentSignComponent,
    PerunFooterCstComponent,
    PerunHeaderComponent,
    ToolsComponent,
    AttributeValuePipe,
    DocumentSignItemLocalePipe
  ],
  imports: [
      AppRoutingModule,
      BrowserModule,
      CoreModule,
      FacilitiesModule,
      HttpClientModule,
      MainMenuModule,
      SharedModule,
      TranslateModule.forRoot({
          loader: {
              provide: TranslateLoader,
              useFactory: HttpLoaderFactory,
              deps: [HttpClient]
          }
      }),
      BrowserAnimationsModule,
      MatDialogModule,
      MatTabsModule
  ],
  providers: [ ],
  bootstrap: [AppComponent]
})
export class AppModule {}
