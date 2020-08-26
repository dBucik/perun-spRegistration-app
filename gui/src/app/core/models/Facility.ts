import {User} from './User';
import {AttributesEntity} from "./AttributesEntity";

export class Facility extends AttributesEntity {
  constructor(item: any) {
    if (!item) {
      return;
    }
    super(item.attributes);
    this.id = item.id;
    this.perunName = item.perunName;
    this.perunDescription = item.perunDescription;
    this.name = new Map<string, string>();
    if (item.hasOwnProperty('name') && item.name) {
      for (const k of Object.keys(item.name)) {
        this.name.set(k.toLowerCase(), item.name[k]);
      }
    }

    this.description = new Map<string, string>();
    if (item.hasOwnProperty('description') && item.description) {
      for (const k of Object.keys(item.description)) {
        this.description.set(k.toLowerCase(), item.description[k]);
      }
    }

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
  perunName: string;
  perunDescription: string;
  name: Map<string, string>;
  description: Map<string, string>;
  testEnv: boolean;
  activeRequestId: number;
  editable: boolean;
  saml: boolean;
  oidc: boolean;
  protocolType: string;
  admins: User[];

}
