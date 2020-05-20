import {Component, Input, ViewChild} from '@angular/core';
import {DetailViewItem} from "../../../core/models/items/DetailViewItem";
import {DetailItemSubCommentButtonComponent} from "../detail-item-sub-comment/detail-item-sub-comment-button/detail-item-sub-comment-button.component";
@Component({
  selector: 'detail-item-boolean',
  templateUrl: './detail-item-boolean.component.html',
  styleUrls: ['./detail-item-boolean.component.scss']
})
export class DetailItemBooleanComponent {

  @Input() item: DetailViewItem = null;
  @Input() includeComment: boolean = false;
  @Input() isAppAdmin: boolean = false;
  @ViewChild('buttonComponent', {static: false}) public buttonComponent?;

}
