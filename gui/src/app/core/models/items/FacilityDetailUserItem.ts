import {User} from "../User";

export class FacilityDetailUserItem {

  constructor(user: User) {
    this.name = user.getFullName();
    this.email = user.email;
  }

  name: string;
  email: string;
}
