import {Component, OnInit} from '@angular/core';
import {AppComponent} from '../app.component';

@Component({
  selector: 'app-perun-header',
  templateUrl: './perun-header.component.html',
  styleUrls: ['./perun-header.component.scss']
})
export class PerunHeaderComponent {

  constructor( ) { }

  private static header: string;

  public static setHeader(header: string) {
    if (!header) {
      this.header = '<div></div>';
    } else {
      this.header = header;
    }
    PerunHeaderComponent.header = this.header;
  }

  public getHeader(): string {
    return PerunHeaderComponent.header;
  }

}
