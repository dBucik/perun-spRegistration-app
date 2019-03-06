import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'facilityAttributeValue'
})
export class FacilityAttributeValuePipe implements PipeTransform {

  transform(value: any, args?: any): any {
    if (value instanceof Object && !(value instanceof Array)) {
      let text = "";
      for (let key of Object.keys(value)) {
        if (text.length > 0) {
          text += ', ';
        }
        text += key + " : " + value[key];
      }
      return text;
    }
    return value;
  }

}
