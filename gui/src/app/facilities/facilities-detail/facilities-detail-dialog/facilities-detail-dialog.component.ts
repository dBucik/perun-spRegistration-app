import {Component, Inject, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {DialogData} from "../facilities-detail.component";

@Component({
  selector: 'app-facilities-detail-dialog',
  templateUrl: './facilities-detail-dialog.component.html',
  styleUrls: ['./facilities-detail-dialog.component.scss']
})
export class FacilitiesDetailDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<FacilitiesDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) { }

  ngOnInit() { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void{
    this.data.parent.loading = true;
    this.data.parent.deleteFacility();
    this.dialogRef.close();
  }

}
