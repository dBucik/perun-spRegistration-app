export class RequestSignature {

  constructor(item: any) {
    this.requestId = item.requestId;
    this.userId = item.userId;
    this.name = item.name;
    this.signedAt = item.signedAt;
    this.approved = item.approved;
  }

  requestId: number;
  userId: number;
  name: string;
  signedAt: string;
  approved: boolean;
}
