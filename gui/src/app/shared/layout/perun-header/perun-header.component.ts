import { Component, OnInit } from '@angular/core';
import { faDatabase, faEnvelope, faHome } from "@fortawesome/free-solid-svg-icons";
import { NavigationEnd, Router } from "@angular/router";

@Component({
  selector: 'app-perun-header',
  templateUrl: './perun-header.component.html',
  styleUrls: ['./perun-header.component.scss']
})
export class PerunHeaderComponent implements OnInit {

  constructor(private router: Router) {
    router.events.subscribe((_: NavigationEnd) => {
      this.currentUrl = this.router.url;
      console.log(this.currentUrl);
    });
  }

  faHome = faHome;
  faRequests = faEnvelope;
  faFacilities = faDatabase;

  currentUrl: string;

  ngOnInit() {
  }

}
