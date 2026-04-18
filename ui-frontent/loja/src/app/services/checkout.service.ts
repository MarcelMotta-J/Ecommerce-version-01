import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Purchase } from '../common/purchase';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class CheckoutService {

  private purchaseUrl = API_ENDPOINTS.CHECKOUT_PURCHASE;

  constructor( private httpClient: HttpClient ){  }

  placeOrder(purchase: Purchase): Observable<any> {
    return this.httpClient.post<Purchase>(this.purchaseUrl, purchase);
  }

 

  
  
}
