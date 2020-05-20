import {Component, Input, ViewChild} from '@angular/core';
import {DetailViewItem} from "../../../core/models/items/DetailViewItem";
import {DetailItemSubCommentButtonComponent} from "../detail-item-sub-comment/detail-item-sub-comment-button/detail-item-sub-comment-button.component";
@Component({
  selector: 'detail-item-map',
  templateUrl: './detail-item-map.component.html',
  styleUrls: ['./detail-item-map.component.scss']
})
export class DetailItemMapComponent {

  @Input() item: DetailViewItem = null;
  @Input() includeComment: boolean = false;
  @Input() isAppAdmin: boolean = false;
  @ViewChild('buttonComponent', {static: false}) public buttonComponent?;

}
