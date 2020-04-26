import {Component, Input} from '@angular/core';
@Component({
  selector: 'detail-item-sub-comment-button',
  templateUrl: './detail-item-sub-comment-button.component.html',
  styleUrls: ['./detail-item-sub-comment-button.component.scss']
})
export class DetailItemSubCommentButtonComponent {

  @Input() isAppAdmin: boolean = false;
  @Input() showComment: boolean = false;
  @Input() includeComment: boolean = false;

  toggleComment(): void {
    this.showComment = !this.showComment;
  }

}
