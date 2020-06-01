import {User} from "../User";

export class FacilityDetailUserItem {

  constructor(user: User) {
    this.name = user.name;
    this.email = user.email;
  }

  name: string;
  email: string;
}
