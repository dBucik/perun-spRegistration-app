import { Injectable } from '@angular/core';
import { ApiService } from "./api.service";
import { Observable } from "rxjs";
import { Facility } from "../models/Facility";

@Injectable({
  providedIn: 'root'
})
export class FacilitiesService {

  constructor(
    private apiService: ApiService
  ) { }

  getAllFacilities(): Observable<Facility[]> {
    return this.apiService.get('/allFacilities');
  }

  getMyFacilities(): Observable<Facility[]> {
    return this.apiService.get('/myFacilities');
  }
}
