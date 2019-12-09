import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { HostListener } from '@angular/core';
import { UsersService } from './core/services/users.service';
import { ConfigService } from './core/services/config.service';
import { PageConfig } from './core/models/PageConfig';
import { User } from './core/models/User';
import { PerunFooterCstComponent } from './perun-footer-cst/perun-footer-cst.component';
import { PerunHeaderComponent } from './perun-header/perun-header.component';

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
    this.onResize();

    const browserLang = translate.getBrowserLang();

    if (!AppComponent.supportedLangs.includes(browserLang)) {
      translate.setDefaultLang('en');
    } else {
      translate.setDefaultLang(browserLang);
    }

    this.currentUrl = this.router.url;

    router.events.subscribe(_ => {
      this.currentUrl = this.router.url;
      if (this.currentUrl.includes('auth') && !this.hasUser()) {
        this.setAndGetUser();
      }
    });
    this.setAndGetUser();
  }

  static supportedLangs: Array<string> = ['en', 'cs'];
  static pageConfig: PageConfig;
  static user: User;

  sidenavOpen = true;
  loading = true;
  minWidth = 768;
  sidenavMode = 'side';
  currentUrl = '';
  logoUrl = '';
  appTitle = '';
  footerHtml = '<div></div>';
  headerHtml = '<div></div>';

  lastWindowWidth: number;

  public static isApplicationAdmin(): boolean {
    if (this.user === undefined || this.user === null) {
      return false;
    }

    return this.user.isAppAdmin;
  }

  public static getPageConfig(): PageConfig {
    return this.pageConfig;
  }

  public static getUser(): User {
    if (this.user === undefined || this.user === null) {
      return null;
    }

    return this.user;
  }

  public static setUser(user: User): void {
    this.user = user;
  }

  public static hasUser() {
    return (AppComponent.getUser() !== null && AppComponent.getUser() !== undefined);
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
    this.setAndGetUser();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event?) {
    if (this.sidenavOpen && this.lastWindowWidth > window.innerWidth &&
      window.innerWidth < this.minWidth) {
      this.sidenavOpen = false;
    }

    this.sidenavMode = window.innerWidth > this.minWidth ? 'side' : 'over';
    this.lastWindowWidth = window.innerWidth;
  }

  public toggleSideBar() {
    this.sidenavOpen = !this.sidenavOpen;
  }

  public hasUser(): boolean {
    return (AppComponent.getUser() !== null && AppComponent.getUser() !== undefined);
  }

  public getUser(): User {
    return AppComponent.user;
  }

  private setAndGetUser() {
    this.userService.getUser().subscribe(user => {
      if (user !== undefined && user !== null) {
        AppComponent.setUser(user);
      } else {
        this.goOnLogin();
      }
    });
  }

  private goOnLogin() {
    this.router.navigate(['/']);
    AppComponent.setUser(undefined);
  }
}
