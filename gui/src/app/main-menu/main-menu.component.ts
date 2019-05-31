import {Component} from '@angular/core';
import {faClipboardList, faNotesMedical, faServer} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss']
})
export class MainMenuComponent {

  requestsIcon = faClipboardList;
  facilitiesIcon = faServer;
  newRequestIcon = faNotesMedical;

  value = "asdf";
}
