import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'requestActionPipe',
  pure: false
})
export class RequestActionPipe implements PipeTransform {

  private returnData: string = "";

  constructor() { }

  transform(value: any, args?: any): any {
    let key = '';
    switch(value) {
      case "REGISTER_NEW_SP": {
        key = 'REQUESTS.ACTIONPIPE.REGISTER_NEW_SP';
        break;
      }
      case "UPDATE_FACILITY": {
        key = 'REQUESTS.ACTIONPIPE.UPDATE_FACILITY';
        break;
      }
      case "DELETE_FACILITY": {
        key = 'REQUESTS.ACTIONPIPE.DELETE_FACILITY';
        break;
      }
      case "MOVE_TO_PRODUCTION": {
        key = 'REQUESTS.ACTIONPIPE.MOVE_TO_PRODUCTION';
        break;
      }
      default: {
        this.returnData = value;
        break;
      }
    }
    return key;
  }

}
