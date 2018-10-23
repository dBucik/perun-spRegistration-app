import { Component, OnInit } from '@angular/core';
import {getHostElement} from "@angular/core/src/render3";

@Component({
  selector: 'app-new-request',
  templateUrl: './new-request.component.html',
  styleUrls: ['./new-request.component.scss']
})
export class NewRequestComponent implements OnInit {

  constructor() { }

  public isForm1Visible = false;
  public isForm2Visible = false;
  public isCardBodyVisible = false;

  ngOnInit() {
  }

}
