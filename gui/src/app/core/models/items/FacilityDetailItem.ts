import {PerunAttribute} from "../PerunAttribute";

export class FacilityDetailItem {

  constructor(urn: string, attr: PerunAttribute) {
    this.urn = urn;
    this.value = attr.value;
    this.name = attr.definition.displayName;
    this.description = attr.definition.description;
  }

  urn: string;
  value: any;
  name: string;
  description: string;
}
