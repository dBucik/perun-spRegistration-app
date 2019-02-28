import { Injectable } from '@angular/core';
import {MatDialog} from "@angular/material";
import {ErrorDialogComponent} from "./error-dialog/error-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) { }

  openDialog(data) : void {
    const dialogRef = this.dialog.open(ErrorDialogComponent, {
      width: '600px',
      data: data
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
    });
  }
}
