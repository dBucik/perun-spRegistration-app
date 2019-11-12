import {PerunAttribute} from './PerunAttribute';
import {User} from './User';

export interface Facility {
  id: number;
  name: string;
  description: string;
  testEnv: boolean;
  activeRequestId: number;
  editable: boolean;
  saml: boolean;
  oidc: boolean;
  attrs: Map<string, PerunAttribute>;
  admins: User[];
}
