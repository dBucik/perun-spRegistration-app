import {Component, OnInit, ViewChildren} from '@angular/core';
import {ConfigService} from "../../core/services/config.service";
import {ApplicationItem} from "../../core/models/ApplicationItem";
import {FormGroup} from "@angular/forms";
import {RequestsService} from "../../core/services/requests.service";
import {ApplicationItemComponent} from "./application-item/application-item.component";

@Component({
  selector: 'app-new-request',
  templateUrl: './new-request.component.html',
  styleUrls: ['./new-request.component.scss']
})
export class NewRequestComponent implements OnInit {

  constructor(
    private configService: ConfigService,
    private requestsService: RequestsService) { }

  @ViewChildren(ApplicationItemComponent)
  items: ApplicationItemComponent[];

  isFormVisible = false;
  isCardBodyVisible = false;
  oidcEnabled: boolean;
  loading = true;
  selected = "";

  applicationItems: ApplicationItem[];

  ngOnInit() {
    this.requestsService.login().subscribe();

    this.configService.isOidcEnabled().subscribe(isEnabled => {
      this.oidcEnabled = isEnabled;
      this.loading = false;
    });
  }

  revealForm() {
    this.loading = false;
    this.isCardBodyVisible = true;
    this.isFormVisible = true;
  }

  onLoading() {
    this.loading = true;
    this.isCardBodyVisible = false;
  }

  oidcSelected() {
    this.onLoading();
    this.selected = "oidc";

    this.configService.getOidcApplicationItems().subscribe(items => {
      this.applicationItems = NewRequestComponent.sortItems(items);
      this.revealForm();
    });
  }

  samlSelected() {
    this.onLoading();
    this.selected = "saml";

    this.configService.getSamlApplicationItems().subscribe(items => {
      this.applicationItems = NewRequestComponent.sortItems(items);
      this.revealForm();
    })
  }

  submitRequest() {
    console.log(this.items);
    this.items.forEach(i => console.log(i.getAttribute()));
  }

  private static getItemOrderValue(item : ApplicationItem) : number {
    let value;

    switch (item.type) {
      case 'java.lang.String':
        value = 0;
        break;
      case 'java.util.ArrayList':
        if (item.allowedValues !== null) {
          value = 1;
        } else {
          value = 2;
        }
        break;
      case 'java.util.LinkedHashMap':
        value = 3;
        break;
      case 'java.lang.Boolean':
        value = 4;
        break;
      default:
        value = 5;
    }

    return value;
  }

  private static sortItems(items : ApplicationItem[]) : ApplicationItem[] {
    return items.sort(((a, b) => {
      let aValue = NewRequestComponent.getItemOrderValue(a);
      let bValue = NewRequestComponent.getItemOrderValue(b);

      return aValue - bValue;
    }))
  }
}
