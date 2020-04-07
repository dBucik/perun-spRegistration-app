export class User {
  constructor(item: any) {
    this.titleBefore = item.titleBefore;
    this.firstName = item.firstName;
    this.middleName = item.middleName;
    this.lastName = item.lastName;
    this.titleAfter = item.titleAfter;
    this.email = item.email;
    this.facilitiesWhereUserIsAdmin = item.facilitiesWhereUserIsAdmin;
    this.isAppAdmin = item.isAppAdmin;
  }

  titleBefore: string;
  firstName: string;
  middleName: string;
  lastName: string;
  titleAfter: string;
  email: string;
  facilitiesWhereUserIsAdmin: number[];
  isAppAdmin: boolean;

  getFullName(): string {
    let name = '';
    if (this.titleBefore) {
      name += this.titleBefore;
    }
    if (this.firstName) {
      name += name.length > 0 ? (' ' + this.firstName) : this.firstName;
    }
    if (this.middleName) {
      name += name.length > 0 ? (' ' + this.middleName) : this.middleName;
    }
    if (this.lastName) {
      name += name.length > 0 ? (' ' + this.lastName) : this.lastName;
    }
    if (this.titleAfter) {
      name += name.length > 0 ? (' ' + this.titleAfter) : this.titleAfter;
    }

    return name;
  }
}
