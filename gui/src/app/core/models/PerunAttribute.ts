import {PerunAttributeDefinition} from "./PerunAttributeDefinition";
import {ApplicationItem} from "./ApplicationItem";

export class PerunAttribute {
  definition: PerunAttributeDefinition;
  value: any;
  oldValue: any;
  comment: string;
  fullName: string;
  input: ApplicationItem;


  constructor(value: any, fullName: string) {
    this.value = value;
    this.fullName = fullName;
  }
}
