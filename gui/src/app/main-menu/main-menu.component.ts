import {Component} from '@angular/core';
import {faClipboardList, faNotesMedical, faServer, faToolbox} from '@fortawesome/free-solid-svg-icons';
import {User} from '../core/models/User';
import {AppComponent} from '../app.component';

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss']
})
export class MainMenuComponent {

  constructor() { }

  requestsIcon = faClipboardList;
  facilitiesIcon = faServer;
  newRequestIcon = faNotesMedical;
  toolboxIcon = faToolbox;

  getUser(): User {
    return AppComponent.getUser();
  }

  hasUser(): boolean {
    return AppComponent.hasUser();
  }

  isUserAppAdmin(): boolean {
    return this.getUser().isAppAdmin;
  }
}
