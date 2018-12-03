import {PerunAttributeDefinition} from "./PerunAttributeDefinition";

export class PerunAttribute {
  definition: PerunAttributeDefinition;
  value: any;
  oldValue: any;
  comment: string;
  fullName: string;


  constructor(value: any, fullName: string) {
    this.value = value;
    this.fullName = fullName;
  }
}
