import { Component, OnInit } from '@angular/core';
import {faEnvelope, faHome, faDatabase} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-perun-sidebar',
  templateUrl: './perun-sidebar.component.html',
  styleUrls: ['./perun-sidebar.component.scss']
})
export class PerunSidebarComponent implements OnInit {

  faHome = faHome;
  faRequests = faEnvelope;
  faFacilities = faDatabase;

  constructor() { }

  ngOnInit() {
  }

}
