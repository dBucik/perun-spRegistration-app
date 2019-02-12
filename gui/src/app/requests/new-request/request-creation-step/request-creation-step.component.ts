import {Component, Input, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ApplicationItem} from "../../../core/models/ApplicationItem";
import {MatStepper} from "@angular/material";
import {ApplicationItemComponent} from "../application-item/application-item.component";
import {PerunAttribute} from "../../../core/models/PerunAttribute";

@Component({
  selector: 'app-request-creation-step',
  templateUrl: './request-creation-step.component.html',
  styleUrls: ['./request-creation-step.component.scss']
})
export class RequestCreationStepComponent implements OnInit {

  constructor() { }

  @ViewChildren(ApplicationItemComponent)
  items: QueryList<ApplicationItemComponent>;

  @Input()
  applicationItems : ApplicationItem[];

  @Input()
  stepper : MatStepper;

  public getPerunAttributes() : PerunAttribute[] {
    let perunAttributes : PerunAttribute[] = [];

    this.items.forEach(i => {
      let attr = i.getAttribute();
      let perunAttr = new PerunAttribute(attr.value, attr.urn);
      perunAttributes.push(perunAttr);
    });

    return perunAttributes;
  }

  private attributesHasCorrectValues() : boolean {

    let attributeItems = this.items.toArray();

    for (const i of attributeItems) {
      if (!i.hasCorrectValue()) {
        return false;
      }
    }

    return true;
  }

  private checkValues() : boolean {
    this.items.forEach(i => i.onFormSubmitted());

    if (!this.attributesHasCorrectValues()) {
      // this.snackBar.open(this.errorText, null, {duration: 6000});
      return false;
    }

    return true;
  }

  nextStep(){
    if (!this.checkValues()) {
      return;
    }
    this.stepper.next();
  }

  previousStep(){
    this.stepper.previous();
  }

  ngOnInit() {
  }

}
