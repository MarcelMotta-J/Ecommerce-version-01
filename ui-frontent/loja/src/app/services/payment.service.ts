import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_ENDPOINTS } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {

  private paymentUrl = API_ENDPOINTS.PAYMENTS;

  constructor(private http: HttpClient) { }



  createPreference(orderId: number): Observable<any> {
    return this.http.post<any>(
      `${this.paymentUrl}/create-preference`,
      { orderId }
    );
  }


}
