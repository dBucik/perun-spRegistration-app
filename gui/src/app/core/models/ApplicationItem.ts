export class ApplicationItem {

  constructor(item: any) {
    if (!item) {
      return
    }
    this.name = item.name;

    this.description = new Map<string, string>();
    for (const k of Object.keys(item.description)) {
      this.description.set(k, item.description[k]);
    }

    this.displayName = new Map<string, string>();
    for (const k of Object.keys(item.displayName)) {
      this.displayName.set(k.toLowerCase(), item.displayName[k]);
    }

    this.required = item.required;
    this.displayPosition = item.displayPosition;
    this.displayed = item.displayed;
    this.regex = item.regex;
    this.type = item.type;
    this.allowedKeys = item.allowedKeys;
    this.allowedValues = item.allowedValues;
    this.comment = item.comment;
    this.oldValue = item.oldValue;
    this.isEdit = item.isEdit;
  }

  name: string;
  required: boolean;
  displayPosition: number;
  displayed: boolean;
  regex: string;
  description: Map<string, string>;
  displayName: Map<string, string>;
  type: string;
  allowedKeys: string[];
  allowedValues: string[];
  comment: string;
  oldValue: any;
  isEdit: boolean;
}
