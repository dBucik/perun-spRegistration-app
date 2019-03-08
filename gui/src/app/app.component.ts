import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {NavigationEnd, Router} from "@angular/router";
import { HostListener } from "@angular/core";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  sideBarOpened = false;

  lastScreenWidth: number;

  sidebarMode = 'side';
  currentUrl: string;

  constructor(
    private translate: TranslateService,
    private router: Router
  ) {
    this.getScreenSize();

    translate.setDefaultLang('en');

    // TODO remove on production
    translate.use('en');
    router.events.subscribe((_: NavigationEnd) => {
      this.currentUrl = this.router.url;
    });
  }

  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {

    if (window.innerWidth > 576) {
      this.sideBarOpened = true;
      this.sidebarMode = 'side';
    } else if (this.lastScreenWidth > 576) {
      this.sideBarOpened = false;
    }

    if (window.innerWidth <= 576) {
      this.sidebarMode = 'over';
    }

    this.lastScreenWidth = window.innerWidth;
  }

    closeSideBar() {
        if (this.sidebarMode == 'over') {
            this.sideBarOpened = false;
        }
    }
}
