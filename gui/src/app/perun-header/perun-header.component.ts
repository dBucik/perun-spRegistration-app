import {Component, OnInit} from '@angular/core';
import {AppComponent} from '../app.component';

@Component({
  selector: 'app-perun-header',
  templateUrl: './perun-header.component.html',
  styleUrls: ['./perun-header.component.scss']
})
export class PerunHeaderComponent implements OnInit {

  constructor( ) { }

  private static header: string;

  public static setHeader(header: string) {
    PerunHeaderComponent.header = header;
  }

  public getHeader(): string {
    return PerunHeaderComponent.header;
  }
  ngOnInit(): void {
    if (AppComponent.getPageConfig() !== null && AppComponent.getPageConfig() !== undefined) {
      PerunHeaderComponent.header = AppComponent.getPageConfig().headerHtml;
    }
  }
}