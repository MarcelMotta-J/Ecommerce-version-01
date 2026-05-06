import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


import { Review } from '../../common/user/review';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})

export class ReviewService {

  private baseUrl = API_ENDPOINTS.REVIEWS;

  constructor(private http: HttpClient) { }

  getReviews(productId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/product/${productId}`);
  }

  createReview(review: any) {

    return this.http.post<Review>(this.baseUrl, review);
  }
}
