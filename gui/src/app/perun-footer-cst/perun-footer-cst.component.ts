import {Component, OnInit} from '@angular/core';
import {AppComponent} from '../app.component';

@Component({
  selector: 'app-perun-footer-cst',
  templateUrl: './perun-footer-cst.component.html',
  styleUrls: ['./perun-footer-cst.component.scss']
})
export class PerunFooterCstComponent implements OnInit {

  constructor( ) { }

  private static footer: string;

  public static setFooter(footer: string) {
    PerunFooterCstComponent.footer = footer;
  }

  public getFooter(): string {
    return PerunFooterCstComponent.footer;
  }

  ngOnInit(): void {
    if (AppComponent.getPageConfig() !== null && AppComponent.getPageConfig() !== undefined) {
      PerunFooterCstComponent.footer = AppComponent.getPageConfig().footerHtml;
    }
  }

}
