import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ConfigService} from "../../core/services/config.service";
import {ApplicationItem} from "../../core/models/ApplicationItem";
import {RequestsService} from "../../core/services/requests.service";
import {MatSnackBar} from "@angular/material";
import {TranslateService} from "@ngx-translate/core";
import {PerunAttribute} from "../../core/models/PerunAttribute";
import {RequestCreationStepComponent} from "./request-creation-step/request-creation-step.component";

@Component({
  selector: 'app-new-request',
  templateUrl: './new-request.component.html',
  styleUrls: ['./new-request.component.scss']
})
export class NewRequestComponent implements OnInit {

  constructor(
    private configService: ConfigService,
    private requestsService: RequestsService,
    private snackBar: MatSnackBar,
    private translate: TranslateService) { }

  @ViewChildren(RequestCreationStepComponent)
  steps: QueryList<RequestCreationStepComponent>;

  serviceSelected : string;

  isFormVisible = false;
  isCardBodyVisible = false;
  oidcEnabled: boolean;
  loading = true;
  selected = "";

  // translations
  errorText : string;
  successfullySubmittedText: string;

  applicationItemGroups: ApplicationItem[][];

  ngOnInit() {
    this.requestsService.login().subscribe();

    this.configService.isOidcEnabled().subscribe(isEnabled => {
      this.oidcEnabled = isEnabled;
      this.loading = false;
    },error => {
      this.loading = false;
      console.log(error);
    });

    this.translate.get('REQUESTS.NEW_VALUES_ERROR_MESSAGE')
      .subscribe(value => this.errorText = value);
    this.translate.get('REQUESTS.SUCCESSFULLY_SUBMITTED_MESSAGE')
      .subscribe(value => this.successfullySubmittedText = value);
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
      this.applicationItemGroups = NewRequestComponent.sortItems(items);
      this.revealForm();
    });
  }

  samlSelected() {
    this.onLoading();
    this.selected = "saml";

    this.configService.getSamlApplicationItems().subscribe(items => {
      this.applicationItemGroups = NewRequestComponent.sortItems(items);
      this.revealForm();
    })
  }

  submitRequest() {

    let perunAttributes : PerunAttribute[] = [];

    this.steps.forEach(step => perunAttributes = perunAttributes.concat(step.getPerunAttributes()));

    console.log(perunAttributes);

    this.requestsService.createRegistrationRequest(perunAttributes).subscribe(requestId => {
      this.snackBar.open(this.successfullySubmittedText, null, {duration: 6000});
    }, error => {
      console.log("Error");
      console.log(error);
    })
  }

  private static sortItems(items : ApplicationItem[][]) : ApplicationItem[][] {
    let sortedItems : ApplicationItem[][] = [];

    items.forEach(itemsGroup => {
      sortedItems.push(itemsGroup.sort(((a, b) => {
        return a.displayPosition - b.displayPosition;
      })))
    });

    return sortedItems;
  }
}
