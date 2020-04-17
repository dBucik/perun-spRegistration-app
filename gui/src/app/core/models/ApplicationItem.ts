export class ApplicationItem {

  constructor(item: any) {
    if (!item) {
      return
    }
    this.name = item.name;
    this.required = item.required;
    this.displayPosition = item.displayPosition;
    this.displayed = item.displayed;
    this.regex = item.regex;
    this.description = item.description;
    this.displayName = item.displayName;
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
