import {PerunAttribute} from "../PerunAttribute";

export class RequestDetailItem {
  constructor(attr: PerunAttribute) {
    if (!attr) {
      return;
    }
    this.urn = attr.fullName;
    this.value = attr.value;
    this.oldValue = attr.oldValue;
    this.comment = attr.comment;

    this.name = new Map<string, string>();
    this.description = new Map<string, string>();

    if (attr.input) {
      this.position = attr.input.displayPosition;

      if (attr.input.displayName) {
        this.name.set("en", attr.input.displayName.get("en"));
        if (attr.input.displayName.get("cs")) {
          this.name.set("cs", attr.input.displayName.get("cs"));
        }
      } else {
        this.name.set("en", attr.definition.displayName);
      }

      if (attr.input.description) {
        this.description.set("en", attr.input.description.get("en"));
        if (attr.input.displayName.get("cs")) {
          this.description.set("cs", attr.input.description.get("cs"));
        }
      } else {
        this.description.set("en", attr.definition.description);
      }
    } else {
      this.position = 0;
    }
  }

  urn: string;
  value: any;
  oldValue: any;
  name: Map<string, string>;
  comment: string;
  description: Map<string, string>;
  position: number;
}
