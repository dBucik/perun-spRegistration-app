import {Component, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {ConfigService} from "../../../core/services/config.service";


@Component({
  selector: 'app-perun-header',
  template: "<div [innerHTML]=\"headerHTML\"></div>",
  //templateUrl: './perun-header.component.html',
  styleUrls: ['./perun-header.component.scss']
})
export class PerunHeaderComponent implements OnInit {

  constructor(private configService: ConfigService) {}

  headerHTML : string;
  headerSubscription : Subscription;

  ngOnInit() {
    this.headerSubscription = this.configService.getHeader().subscribe(header => {
      this.headerHTML = header;
    })
  }
}
