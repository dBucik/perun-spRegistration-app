import { Injectable } from '@angular/core';
import { ApiService } from "./api.service";
import { Observable } from "rxjs";
import { Request } from "../models/Request";

@Injectable({
  providedIn: 'root'
})
export class RequestsService {

  constructor(
    private apiService: ApiService
  ) { }

  //TODO delete THIS!!!
  login() {
    return this.apiService.get('');
  }

  getAllRequests(): Observable<Request[]> {
    return this.apiService.get('/allRequests');
  }
}
