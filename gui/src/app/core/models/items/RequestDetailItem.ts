import {PerunAttribute} from "../PerunAttribute";

export class RequestDetailItem {
  constructor(attr: PerunAttribute) {
    this.urn = attr.fullName;
    this.value = attr.value;
    this.oldValue = attr.oldValue;
    this.comment = attr.comment;
    this.name = attr.definition.displayName;
    this.description = attr.definition.description;
    this.position = attr.input.displayPosition;
  }

  urn: string;
  value: any;
  oldValue: any;
  name: string;
  comment: string;
  description: string;
  position: number;
}
