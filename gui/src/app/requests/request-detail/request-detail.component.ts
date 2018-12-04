import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-request-detail',
  templateUrl: './request-detail.component.html',
  styleUrls: ['./request-detail.component.scss']
})
export class RequestDetailComponent implements OnInit, OnDestroy {

  constructor(private route: ActivatedRoute) { }

  private sub : Subscription;

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      console.log(params['id']);
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
