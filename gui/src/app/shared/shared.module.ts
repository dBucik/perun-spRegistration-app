import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SortableTableDirective} from './layout/sortable-table.directive';
import {SortableColumnComponent} from "./layout/sortable-column.component";
import {TranslateModule} from '@ngx-translate/core';
import {PerunFooterComponent} from './layout/perun-footer/perun-footer.component';
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {RouterModule} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { MatBadgeModule } from "@angular/material/badge";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatChipsModule } from "@angular/material/chips";
import { MatDialogModule } from "@angular/material/dialog";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatMenuModule } from "@angular/material/menu";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatRadioModule } from "@angular/material/radio";
import { MatSelectModule } from "@angular/material/select";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatSortModule } from "@angular/material/sort";
import { MatStepperModule } from "@angular/material/stepper";
import { MatTableModule } from "@angular/material/table";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatTooltipModule } from "@angular/material/tooltip";
import { ErrorDialogComponent } from './error-dialog/error-dialog.component';
import { NotFoundPageComponent } from './not-found-page/not-found-page.component';
import { NotAuthorizedPageComponent } from './not-authorized-page/not-authorized-page.component';
import {ApplicationItemComponent} from "../requests/new-request/application-item/application-item.component";
import {ApplicationItemStringComponent} from "../requests/new-request/application-item/application-item-string/application-item-string.component";
import {ApplicationItemBooleanComponent} from "../requests/new-request/application-item/application-item-boolean/application-item-boolean.component";
import {ApplicationItemListComponent} from "../requests/new-request/application-item/application-item-list/application-item-list.component";
import {ApplicationItemMapComponent} from "../requests/new-request/application-item/application-item-map/application-item-map.component";
import {ApplicationItemSelectComponent} from "../requests/new-request/application-item/application-item-select/application-item-select.component";
import {DetailItemSubCommentButtonComponent} from "./detailed-view-items/detail-item-sub-comment/detail-item-sub-comment-button/detail-item-sub-comment-button.component";
import {DetailItemSubCommentUserComponent} from "./detailed-view-items/detail-item-sub-comment/detail-item-sub-comment-user/detail-item-sub-comment-user.component";
import {DetailItemSubCommentAdminComponent} from "./detailed-view-items/detail-item-sub-comment/detail-item-sub-comment-admin/detail-item-sub-comment-admin.component";
import {DetailItemSubCommentComponent} from "./detailed-view-items/detail-item-sub-comment/detail-item-sub-comment.component";
import {DetailItemStringComponent} from "./detailed-view-items/detail-item-string/detail-item-string.component";
import {DetailItemMapComponent} from "./detailed-view-items/detail-item-map/detail-item-map.component";
import {DetailItemIntegerComponent} from "./detailed-view-items/detail-item-integer/detail-item-integer.component";
import {DetailItemBooleanComponent} from "./detailed-view-items/detail-item-boolean/detail-item-boolean.component";
import {DetailItemArrayComponent} from "./detailed-view-items/detail-item-array/detail-item-array.component";
import {DetailedViewItemsComponent} from "./detailed-view-items/detailed-view-items.component";
import {DetailedViewItemValuePipe} from "./detailed-view-item-value.pipe";
import {ItemLocalePipe} from "./item-locale.pipe";

@NgModule({
  imports: [
    RouterModule,
    MatProgressBarModule,
    CommonModule,
    FontAwesomeModule,
    FormsModule,
    MatSelectModule,
    MatInputModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatTooltipModule,
    MatButtonModule,
    MatToolbarModule,
    MatSnackBarModule,
    MatIconModule,
    MatTableModule,
    MatSortModule,
    TranslateModule,
    MatStepperModule,
    MatSidenavModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatExpansionModule,
    MatChipsModule,
    MatMenuModule,
    MatBadgeModule
  ],
  declarations: [
    SortableColumnComponent,
    SortableTableDirective,
    PerunFooterComponent,
    ErrorDialogComponent,
    NotFoundPageComponent,
    NotAuthorizedPageComponent,
    ApplicationItemComponent,
    ApplicationItemStringComponent,
    ApplicationItemBooleanComponent,
    ApplicationItemListComponent,
    ApplicationItemMapComponent,
    ApplicationItemSelectComponent,
    DetailedViewItemsComponent,
    DetailItemArrayComponent,
    DetailItemBooleanComponent,
    DetailItemIntegerComponent,
    DetailItemMapComponent,
    DetailItemStringComponent,
    DetailItemSubCommentComponent,
    DetailItemSubCommentAdminComponent,
    DetailItemSubCommentUserComponent,
    DetailItemSubCommentButtonComponent,
    DetailedViewItemValuePipe,
    ItemLocalePipe
  ],
    exports: [
        FontAwesomeModule,
        SortableColumnComponent,
        SortableTableDirective,
        TranslateModule,
        PerunFooterComponent,
        FormsModule,
        MatSelectModule,
        MatInputModule,
        MatCheckboxModule,
        MatProgressSpinnerModule,
        MatRadioModule,
        MatTooltipModule,
        MatButtonModule,
        MatToolbarModule,
        MatSnackBarModule,
        MatIconModule,
        MatTableModule,
        MatSortModule,
        MatSidenavModule,
        MatStepperModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatExpansionModule,
        ReactiveFormsModule,
        MatProgressBarModule,
        MatChipsModule,
        MatMenuModule,
        MatMenuModule,
        MatBadgeModule,
        ApplicationItemComponent,
        DetailedViewItemsComponent,
        DetailItemArrayComponent,
        DetailItemBooleanComponent,
        DetailItemIntegerComponent,
        DetailItemMapComponent,
        DetailItemStringComponent,
        DetailItemSubCommentComponent,
        DetailItemSubCommentAdminComponent,
        DetailItemSubCommentUserComponent,
        DetailItemSubCommentButtonComponent,
        ItemLocalePipe,
        DetailedViewItemValuePipe
    ],
  entryComponents: [
    ErrorDialogComponent
  ]
})
export class SharedModule {
}
