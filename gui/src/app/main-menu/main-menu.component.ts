import {Component} from '@angular/core';
import {faClipboardList, faNotesMedical, faServer} from "@fortawesome/free-solid-svg-icons";
import {OnInit} from "@angular/core/src/metadata/lifecycle_hooks";
import {AppComponent} from "../app.component";
import {UsersService} from "../core/services/users.service";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss']
})
export class MainMenuComponent implements OnInit{

  constructor(private userService: UsersService) {

  }

  requestsIcon = faClipboardList;
  facilitiesIcon = faServer;
  newRequestIcon = faNotesMedical;

  value = "asdf";

  ngOnInit(): void {
    this.userService.getUser().subscribe(user => {
      AppComponent.setUser(user);
    });
  }
}
