import {Pipe, PipeTransform} from '@angular/core';
import {Facility} from '../core/models/Facility';

@Pipe({
  name: 'facilityProtocol'
})
export class FacilityProtocolPipe implements PipeTransform {

  private saml = 'SAML';
  private oidc = 'OIDC';

  constructor() { }

  transform(facility: Facility, args?: any): any {
    if (facility.oidc && facility.saml) {
      return this.saml + '+' + this.oidc;
    } else if (facility.oidc) {
      return this.oidc;
    } else if (facility.saml) {
      return this.saml;
    } else {
      return '';
    }
  }

}
