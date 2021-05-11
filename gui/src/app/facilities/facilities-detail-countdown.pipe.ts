import { Pipe, PipeTransform } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Pipe({
  name: 'countdown'
})
export class FacilitiesDetailCountdownPipe implements PipeTransform {

  private finishedText: string = 'FINISHED';
  private runningText: string = 'RUNNING';

  constructor(
    private translate: TranslateService
  ) {
    this.translate.get('FACILITIES.SYNC.FINISHED').subscribe(text => {
      this.finishedText = text;
    });
    this.translate.get('FACILITIES.SYNC.RUNNING').subscribe(text => {
      this.runningText = text;
    });
  }

  transform(value: number): string {
    if (value <= 0) {
      return this.finishedText;
    } else {
      return this.runningText + ' ' + new Date(value).toISOString().slice(11, -5);
    }
  }
}
