import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AdminPagedResponse } from '../../common/admin/admin-paged-response';
import { AdminOrderRow } from '../../common/admin/admin-order-row';
import { AdminOrderDetailsResponse } from '../../common/admin/AdminOrderDetailsResponse';
import { UpdateOrderStatusRequest } from '../../common/admin/update-order-status-request';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class AdminOrderService {

  private baseUrl = API_ENDPOINTS.ADMIN_ORDERS;

  constructor(private http: HttpClient) { }

  getOrders(
    page: number,
    size: number,
    status?: string | null,
    customerEmail?: string | null
  ): Observable<AdminPagedResponse<AdminOrderRow>> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (status && status.trim()) {
      params = params.set('status', status.trim());
    }

    if (customerEmail && customerEmail.trim()) {
      params = params.set('customerEmail', customerEmail.trim());
    }

    return this.http.get<AdminPagedResponse<AdminOrderRow>>(this.baseUrl, { params });
  }

  getOrderDetails(id: number): Observable<AdminOrderDetailsResponse> {
    return this.http.get<AdminOrderDetailsResponse>(`${this.baseUrl}/${id}`);
  }

  updateOrderStatus(id: number, status: string): Observable<void> {
    const body: UpdateOrderStatusRequest = { status };

    return this.http.patch<void>(`${this.baseUrl}/${id}/status`, body);
  }

}
