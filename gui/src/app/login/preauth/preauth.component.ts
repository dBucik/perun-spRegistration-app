import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../core/services/users.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-preauth',
  templateUrl: './preauth.component.html',
  styleUrls: ['./preauth.component.scss']
})
export class PreauthComponent implements OnInit {

  constructor(private userService: UsersService,
              private router: Router) {
  }

  ngOnInit() {
    this.userService.getUser().subscribe(user => {
      if (user != null) {
        this.router.navigate(['/auth']);
      } else {
        this.userService.setUser().subscribe(success => {
          if (success) {
            this.router.navigate(['/auth']);
          } else {
            this.router.navigate(['/']);
          }
        })
      }
    });
  }

}
