export class User {
  constructor(item: any) {
    this.name = item.name;
    this.email = item.email;
    this.facilitiesWhereUserIsAdmin = item.facilitiesWhereUserIsAdmin;
    this.isAppAdmin = item.isAppAdmin;
  }

  name: string;
  email: string;
  facilitiesWhereUserIsAdmin: number[];
  isAppAdmin: boolean;
}
