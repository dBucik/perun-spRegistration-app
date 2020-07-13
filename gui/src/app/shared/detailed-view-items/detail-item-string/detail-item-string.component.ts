import {Component, Input, ViewChild} from '@angular/core';
import {DetailViewItem} from "../../../core/models/items/DetailViewItem";
import {DetailItemSubCommentButtonComponent} from "../detail-item-sub-comment/detail-item-sub-comment-button/detail-item-sub-comment-button.component";
@Component({
  selector: 'detail-item-string',
  templateUrl: './detail-item-string.component.html',
  styleUrls: ['./detail-item-string.component.scss']
})
export class DetailItemStringComponent {

  @Input() item: DetailViewItem = null;
  @Input() includeComment: boolean = false;
  @Input() isAppAdmin: boolean = false;
  @Input() displayOldVal: boolean = false;
  @ViewChild('buttonComponent', {static: false}) public buttonComponent?;

}
