export class User {
  constructor(item: any) {
    if (!item) {
      return;
    }
    this.name = item.name;
    if (item.hasOwnProperty('email')) {
      this.email = item.email;
    } else {
      this.email = null;
    }
    this.facilitiesWhereUserIsAdmin = item.facilitiesWhereUserIsAdmin;
    this.isAppAdmin = item.isAppAdmin;
  }

  name: string;
  email: string;
  facilitiesWhereUserIsAdmin: number[];
  isAppAdmin: boolean;

}
