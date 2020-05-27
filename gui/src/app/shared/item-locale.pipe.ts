import { Pipe, PipeTransform } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Pipe({
  name: 'itemLocalePipe',
  pure: false
})
export class ItemLocalePipe implements PipeTransform {

  constructor(private translate: TranslateService) {  }

  transform(value: Map<string, string>, args?: any): any {
    const lang = this.translate.currentLang;

    if (value.has(lang.toLowerCase())) {
      return value.get(lang.toLowerCase());
    }

    return value.get('en');
  }
}

