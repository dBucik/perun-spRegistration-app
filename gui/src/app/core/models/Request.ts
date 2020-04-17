import {AttributesEntity} from "./AttributesEntity";

export class Request extends AttributesEntity {
  constructor(item: any) {
    if (!item) {
      return;
    }

    super(item.attributes);
    this.reqId = item.reqId;
    this.facilityId = item.facilityId;
    this.status = item.status;
    this.action = item.action;
    this.reqUserId = item.reqUserId;
    this.modifiedAt = item.modifiedAt;
    this.modifiedBy = item.modifiedBy;
  }

  reqId: number;
  facilityId: number;
  status: string;
  action: string;
  reqUserId: number;
  modifiedAt: string;
  modifiedBy: number;
}
