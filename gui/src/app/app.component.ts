import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NavigationEnd, Router } from '@angular/router';
import { HostListener } from '@angular/core';
import { UsersService } from './core/services/users.service';
import { ConfigService } from './core/services/config.service';
import { PageConfig } from './core/models/PageConfig';
import { User } from './core/models/User';
import {PerunFooterCstComponent} from './perun-footer-cst/perun-footer-cst.component';
import {PerunHeaderComponent} from './perun-header/perun-header.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(
    private configService: ConfigService,
    private userService: UsersService,
    private translate: TranslateService,
    private router: Router
  ) {
    this.getScreenSize();

    const browserLang = translate.getBrowserLang();

    if (!AppComponent.supportedLangs.includes(browserLang)) {
      translate.setDefaultLang('en');
    } else {
      translate.setDefaultLang(browserLang);
    }

    router.events.subscribe((_: NavigationEnd) => {
      this.currentUrl = this.router.url;
      if (this.currentUrl.includes('auth')) {
        this.userService.getUser().subscribe(user => {
          if (user !== undefined && user !== null) {
            AppComponent.setUser(user);
          }
          this.sideBarOpened = false;
        });
      } else {
        this.sideBarOpened = true;
      }
    });

    this.userService.getUser().subscribe(user => {
      AppComponent.setUser(user);
    });
  }

  static supportedLangs: Array<string> = ['en', 'cs'];

  static sideNavHiddenOn: Array<string> = ['/'];

  static pageConfig: PageConfig;
  static user: User;

  sideBarOpened = false;
  onPageWhereSideBarIsHidden = false;
  loading = true;

  lastScreenWidth: number;

  minWidth = 768;
  sidebarMode = 'side';
  currentUrl: string;

  logoUrl: String = '';
  appTitle: String = '';
  footerHtml = '<div></div>';
  headerHtml = '<div></div>';

  public static isApplicationAdmin(): boolean {
    if (this.user === undefined || this.user === null) {
      return false;
    }
    return this.user.isAppAdmin;
  }

  public static getPageConfig(): PageConfig {
    return this.pageConfig;
  }

  // for usage from different components
  public static getUser(): User {
    if (this.user === undefined || this.user === null) {
      return null;
    }

    return this.user;
  }

  public static setUser(user: User): void {
    this.user = user;
  }

  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {

    if (this.onPageWhereSideBarIsHidden) {
      this.sideBarOpened = false;
      return;
    }

    if (window.innerWidth > this.minWidth) {
      this.sideBarOpened = true;
      this.sidebarMode = 'side';
    } else if (this.lastScreenWidth > this.minWidth) {
      this.sideBarOpened = false;
    }

    if (window.innerWidth <= this.minWidth) {
      this.sidebarMode = 'over';
    }

    this.lastScreenWidth = window.innerWidth;
  }

  closeSideBar() {
      if (this.sidebarMode === 'over') {
          this.sideBarOpened = false;
      }
  }

  ngOnInit(): void {
    this.configService.getPageConfig().subscribe(pageConfig => {
      AppComponent.pageConfig = pageConfig;
      if (pageConfig !== null && pageConfig !== undefined) {
        this.appTitle = pageConfig.headerLabel;
        this.logoUrl = pageConfig.logoUrl;
        this.footerHtml = pageConfig.footerHtml;
        this.headerHtml = pageConfig.headerHtml;
        PerunFooterCstComponent.setFooter(this.footerHtml);
        PerunHeaderComponent.setHeader(this.headerHtml);
      }

      PerunFooterCstComponent.setFooter(this.footerHtml);
      PerunHeaderComponent.setHeader(this.headerHtml);
      this.loading = false;
    });
  }

  // for local usage e.g.: in app.component.html
  public getUser(): User {
    return AppComponent.getUser();
  }
}
