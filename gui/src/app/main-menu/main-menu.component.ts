import {Component, OnInit} from '@angular/core';
import {RequestsService} from "../core/services/requests.service";
import {FacilitiesService} from "../core/services/facilities.service";
import {Request} from "../core/models/Request";
import {RequestStatus} from "../core/models/RequestStatus";
import {RequestAction} from "../core/models/RequestAction";
import {Facility} from "../core/models/Facility";
import {MenuButtonComponent} from "./menu-button/menu-button.component";
import {faDatabase, faEnvelopeOpen, IconDefinition} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss']
})
export class MainMenuComponent implements OnInit {

  menuButtons = [
    {
      icon: faEnvelopeOpen,
      text: 'My requests',
      route: 'requests'
    },
    {
      icon: faDatabase,
      text: 'My facilities',
      route: 'facilities'
    }
  ];

  constructor() { }

  ngOnInit() {
  }
}

interface MenuButton {
  icon: IconDefinition,
  text: string,
  route: string;
}
