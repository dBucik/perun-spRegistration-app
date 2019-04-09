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

  onYesClick(): void{
    if (this.data.isApprove){
      this.data.parent.approve();
    } else {
      this.data.parent.reject();
    }
  }

  ngOnInit() {
    if (this.data.isApprove) {
      this.translate.get("REQUEST.ARE_YOU_SURE_APPROVE").subscribe(value => this.areUsure = value);
    } else {
      this.translate.get("REQUEST.ARE_YOU_SURE_REJECT").subscribe(value => this.areUsure = value);
    }
  }

}
