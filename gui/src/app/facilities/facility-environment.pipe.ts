import {OnInit, Pipe, PipeTransform} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Pipe({
  name: 'facilityEnvironment'
})
export class FacilityEnvironmentPipe implements PipeTransform {

  private testEnvText: string = "TEXT";
  private prodEnvText: string = "TEXT2";

  constructor(
    private translate: TranslateService
  ) {
    this.translate.get('FACILITIES.ENV_PROD').subscribe(text => {
      this.testEnvText = text;
    });
    this.translate.get('FACILITIES.ENV_TEST').subscribe(text => {
      this.prodEnvText = text;
    });
  }

  transform(testEnv: any, args?: any): any {
    if (testEnv) {
      return this.prodEnvText;
    } else {
      return this.testEnvText;
    }
  }

}
