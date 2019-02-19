import {PerunAttribute} from "./PerunAttribute";

export interface Request {
  reqId: number;
  facilityId: number;
  status: string;
  action: string;
  reqUserId: number;
  attributes: Map<string, PerunAttribute>;
  modifiedAt: string;
  modifiedBy: number;
}
