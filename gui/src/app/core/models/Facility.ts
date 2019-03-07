import {PerunAttribute} from "./PerunAttribute";

export interface Facility {
  id: number,
  name: string,
  description: string,
  attrs: Map<string, PerunAttribute>
}
