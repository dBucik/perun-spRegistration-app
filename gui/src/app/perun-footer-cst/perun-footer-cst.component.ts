import {Component, OnInit} from '@angular/core';
import {AppComponent} from '../app.component';
import {PerunFooterComponent} from "../shared/layout/perun-footer/perun-footer.component";

@Component({
  selector: 'app-perun-footer-cst',
  templateUrl: './perun-footer-cst.component.html',
  styleUrls: ['./perun-footer-cst.component.scss']
})
export class PerunFooterCstComponent {

  constructor( ) { }

  private static footer: string;

  public static setFooter(footer: string) {
    if (!footer) {
      this.footer = '<div></div>';
    } else {
      this.footer = footer;
    }
    PerunFooterCstComponent.footer = this.footer;
  }

  public getFooter(): string {
    return PerunFooterCstComponent.footer;
  }

}
