import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {DialogData} from "../request-detail.component";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-request-detail-dialog',
  templateUrl: './request-detail-dialog.component.html',
  styleUrls: ['./request-detail-dialog.component.scss']
})
export class RequestDetailDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<RequestDetailDialogComponent>,
    private translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  areUsure: string;

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    if (this.data.isApprove) {
      this.data.parent.approve();
      this.dialogRef.close();
      return
    }
    if (this.data.isSetWFC) {
      this.data.parent.requestChanges();
      this.dialogRef.close();
      return;
    }
    this.data.parent.reject();
    this.dialogRef.close();
  }

  ngOnInit() {
    if (this.data.isApprove) {
      this.translate.get('REQUESTS.ARE_YOU_SURE_APPROVE').subscribe(value => this.areUsure = value);
      return
    }
    if (this.data.isSetWFC) {
      this.translate.get('REQUESTS.ARE_YOU_SURE_SET_WFC').subscribe(value => this.areUsure = value);
      return;
    }
    this.translate.get('REQUESTS.ARE_YOU_SURE_REJECT').subscribe(value => this.areUsure = value);

  }

}
