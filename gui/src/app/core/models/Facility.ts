import {User} from './User';
import {AttributesEntity} from "./AttributesEntity";

export class Facility extends AttributesEntity {
  constructor(item: any) {
    if (!item) {
      return;
    }
    super(item.attributes);
    this.id = item.id;
    this.name = item.name;
    this.description = item.description;
    this.testEnv = item.testEnv;
    this.activeRequestId = item.activeRequestId;
    this.editable = item.editable;
    this.saml = item.saml;
    this.oidc = item.oidc;
    if (this.saml && this.oidc) {
      this.protocolType = "SAML / OIDC";
    } else if (this.saml) {
      this.protocolType = "SAML";
    } else if (this.oidc) {
      this.protocolType = "OIDC";
    }
    this.admins = item.admins;
  }

  id: number;
  name: string;
  description: string;
  testEnv: boolean;
  activeRequestId: number;
  editable: boolean;
  saml: boolean;
  oidc: boolean;
  protocolType: string;
  admins: User[];

}
