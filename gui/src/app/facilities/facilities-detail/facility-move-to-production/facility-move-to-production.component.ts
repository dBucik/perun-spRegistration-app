import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FacilitiesService} from '../../../core/services/facilities.service';
import {Subscription} from 'rxjs';
import {Facility} from '../../../core/models/Facility';
import { MatSnackBar } from '@angular/material/snack-bar';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {TranslateService} from '@ngx-translate/core';
import {ConfigService} from '../../../core/services/config.service';

@Component({
  selector: 'app-facility-move-to-production',
  templateUrl: './facility-move-to-production.component.html',
  styleUrls: ['./facility-move-to-production.component.scss']
})
export class FacilityMoveToProductionComponent implements OnInit, OnDestroy {

  constructor(
    private route: ActivatedRoute,
    private facilitiesService: FacilitiesService,
    private snackBar: MatSnackBar,
    private translate: TranslateService,
    private configService: ConfigService,
    private router: Router
  ) { }

  private sub: Subscription;
  private authorities: string[];

  loading = true;
  facility: Facility;
  emails: string[] = [];
  selectEmailsEnabled = false;
  specifyFromList = false;

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.facilitiesService.getFacility(params['id']).subscribe(facility => {
        this.configService.isAuthoritiesEnabled().subscribe(response => {
          this.selectEmailsEnabled = response;
          this.specifyFromList = response;
          this.facility = facility;
          this.emails = [];
          this.loading = false;
        });
      });
      this.configService.getProdTransferEntries().subscribe(entries => {
        this.authorities = entries;
      });
    });
  }

  moveToProduction(): void {
    this.loading = true;
    if (!this.selectEmailsEnabled) {
      this.emails = [];
    }

    if (this.selectEmailsEnabled && this.emails.length === 0) {
      this.translate.get('FACILITIES.EMAIL_ERROR_FILL').subscribe(result => {
        this.snackBar.open(result, null, {duration: 5000});
      });
      this.loading = false;
      return;
    }

    this.facilitiesService.createRequest(this.facility.id, this.emails).subscribe(reqid => {
      this.loading = false;
        this.translate.get('FACILITIES.MOVE_TO_PRODUCTION_SUCCESS').subscribe(successMessage => {
          this.translate.get('FACILITIES.SUCCESSFULLY_SUBMITTED_ACTION').subscribe(goToRequestMessage => {
            const snackBarRef = this.snackBar.open(successMessage, goToRequestMessage, {duration: 5000});

            snackBarRef.onAction().subscribe(() => {
                this.router.navigate(['/auth/requests/detail/' + reqid]);
            });

            this.router.navigate(['/auth']);
          });
        });
    });
  }

  checkboxChanged(email: string): void {
    const index = this.emails.indexOf(email);
    if (index >= 0) {
      this.emails.splice(index, 1);
    } else {
      this.emails.push(email);
    }
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
