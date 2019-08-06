import {Component} from '@angular/core';
import {faClipboardList, faNotesMedical, faServer} from "@fortawesome/free-solid-svg-icons";
import {OnInit} from "@angular/core/src/metadata/lifecycle_hooks";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss']
})
export class MainMenuComponent implements OnInit{

  constructor() {

  }

  requestsIcon = faClipboardList;
  facilitiesIcon = faServer;
  newRequestIcon = faNotesMedical;

  value = "asdf";

  ngOnInit(): void {
  }
}
