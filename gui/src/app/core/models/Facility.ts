import {PerunAttribute} from "./PerunAttribute";

export interface Facility {
  id: number,
  name: string,
  description: string,
  testEnv: boolean,
  attrs: Map<string, PerunAttribute>
}
