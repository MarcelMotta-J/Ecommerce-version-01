import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UserProfileResponse } from '../../common/user/user-profile-response';
import { UserAddressResponse } from '../../common/user/user-address-response';
import { UserOrderResponse } from '../../common/user/user-order-response'; 
import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private baseUrl = API_ENDPOINTS.USER;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.baseUrl}/profile`);
  }

  updateProfile(profile: Partial<UserProfileResponse>): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.baseUrl}/profile`, profile);
  }

  getAddresses(): Observable<UserAddressResponse[]> {
    return this.http.get<UserAddressResponse[]>(`${this.baseUrl}/addresses`);
  }
  

  addAddress(address: UserAddressResponse): Observable<UserAddressResponse> {
    return this.http.post<UserAddressResponse>(`${this.baseUrl}/addresses`, address);
  }

  updateAddress(addressId: number, address: UserAddressResponse): Observable<UserAddressResponse> {
    return this.http.put<UserAddressResponse>(`${this.baseUrl}/addresses/${addressId}`, address);
  }

  deleteAddress(addressId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/addresses/${addressId}`);
  }

  setDefaultAddress(addressId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/addresses/${addressId}/default`, {});
  }

  getUserOrders(): Observable<UserOrderResponse[]> {
    return this.http.get<UserOrderResponse[]>(`${this.baseUrl}/orders`);
  }


  


  
}
