import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AdminCustomerRow } from '../../common/admin/admin-customer-row';
import { AdminPagedResponse } from '../../common/admin/admin-paged-response';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class AdminCustomersService {
  private baseUrl = API_ENDPOINTS.ADMIN_CUSTOMERS;

  constructor(private http: HttpClient) { }

  getCustomers(page: number, size: number): Observable<AdminPagedResponse<AdminCustomerRow>> {
    return this.http.get<AdminPagedResponse<AdminCustomerRow>>(this.baseUrl, {
      params: {
        page,
        size
      }
    });
  }

  deleteCustomer(customerId: number) {
    return this.http.delete(`${this.baseUrl}/${customerId}`);
  }

  updateCustomer(customerId: number, customer: any) {
    return this.http.put(`${this.baseUrl}/${customerId}`, customer);
  }

  getCustomerById(customerId: number): Observable<AdminCustomerRow> {
  return this.http.get<AdminCustomerRow>(`${this.baseUrl}/${customerId}`);
}

}
