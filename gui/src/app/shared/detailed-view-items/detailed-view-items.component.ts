import {Component, Input} from '@angular/core';
import {DetailViewItem} from "../../core/models/items/DetailViewItem";
@Component({
  selector: 'detailed-view-items',
  templateUrl: './detailed-view-items.component.html',
  styleUrls: ['./detailed-view-items.component.scss']
})
export class DetailedViewItemsComponent {

  @Input() attrs: DetailViewItem[] = [];
  @Input() isAppAdmin: boolean = false;
  @Input() includeComment: boolean = false;
  @Input() displayOldVal: boolean = false;

  shouldDisplayOldVal(item: DetailViewItem): boolean {
    return this.displayOldVal && item.oldValue;
  }
}
