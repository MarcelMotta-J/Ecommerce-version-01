import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_ENDPOINTS } from '../../core/api.config';
import { Review } from '../../common/user/review';

@Injectable({
  providedIn: 'root',
})
export class AdminReviewService {

  baseUrl = API_ENDPOINTS.ADMIN + '/reviews';

  constructor(private http: HttpClient) { }

  getAll() {
    return this.http.get<Review[]>(this.baseUrl);
  }

  delete(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getStats() {
    return this.http.get<any>(`${this.baseUrl}/stats`);
  }

  getDistribution() {
  return this.http.get<any[]>(`${this.baseUrl}/distribution`);
}

}
