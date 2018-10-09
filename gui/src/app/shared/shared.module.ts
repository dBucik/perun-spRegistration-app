import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SortableTableDirective } from './layout/sortable-table.directive';
import {SortableColumnComponent} from "./layout/sortable-column.component";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    SortableColumnComponent,
    SortableTableDirective
  ],
  exports: [
    SortableColumnComponent,
    SortableTableDirective
  ]
})
export class SharedModule { }
