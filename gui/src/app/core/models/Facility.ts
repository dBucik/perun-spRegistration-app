import {PerunAttribute} from './PerunAttribute';

export interface Facility {
  id: number;
  name: string;
  description: string;
  testEnv: boolean;
  activeRequestId: number;
  canEdit: boolean;
  oidc: boolean;
  attrs: Map<string, PerunAttribute>;
}
