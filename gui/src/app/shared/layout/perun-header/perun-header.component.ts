import { Component, OnInit } from '@angular/core';
import { faDatabase, faEnvelope, faHome } from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-perun-header',
  templateUrl: './perun-header.component.html',
  styleUrls: ['./perun-header.component.scss']
})
export class PerunHeaderComponent implements OnInit {

  constructor() { }

  faHome = faHome;
  faRequests = faEnvelope;
  faFacilities = faDatabase;

  ngOnInit() {
  }

}
