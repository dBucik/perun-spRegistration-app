import {ApplicationItem} from "../../../core/models/ApplicationItem";

export class RequestItemInputUtils {

  constructor() { }

  public static hasValueMultiValue(values: string[]): boolean {
    if (values === undefined || values === null || values.length === 0) {
      return false;
    }

    for (let i = 0; i < values.length; i++) {
      const subValue = values[i];
      if (subValue === undefined || subValue === null || subValue.length === 0) {
        return false;
      }
    }

    return true;
  }

  public static requestedChangeHasBeenMadeMultiValue(appItem: ApplicationItem, values: string[]): boolean {
    if (appItem.hasComment()) {
      if (appItem.oldValue.length !== values.length) {
        return true;
      }

      for (let i = 0; i < values.length; i++) {
        if (appItem.oldValue.indexOf(values[i]) === -1) {
          return true;
        }
      }

      return false;
    }

    return true;
  }

  public static checkRegexMultiValue(item: ApplicationItem, values: string[]): number[] {
    const indexes = [];
    if (item.hasRegex()) {
      const reg = new RegExp(item.regex);
      for (let i = 0; i < values.length; i++) {
        if (!reg.test(values[i])) {
          indexes.push(i);
        }
      }
    }

    return indexes;
  }

}
