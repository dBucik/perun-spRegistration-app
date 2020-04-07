import {PerunAttribute} from "../PerunAttribute";

export class RequestDetailItem {
  constructor(attr: PerunAttribute) {
    this.urn = attr.fullName;
    this.value = attr.value;
    this.oldValue = attr.oldValue;
    this.comment = attr.comment;
    this.name = attr.definition.displayName;
    this.description = attr.definition.description;
  }

  urn: string;
  value: any;
  oldValue: any;
  name: string;
  comment: string;
  description: string;
};
