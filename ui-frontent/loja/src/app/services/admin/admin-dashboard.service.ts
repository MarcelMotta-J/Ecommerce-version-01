import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AdminDashboardResponse } from '../../common/admin/admin-dashboard-response';
import { Observable } from 'rxjs';
import { AdminOrdersPerDayPoint } from '../../common/admin/admin-orders-per-day-point';


import { AdminRevenuePerDayPoint } from '../../common/admin/admin-revenue-per-day-point';
import { AdminTopProductPoint } from '../../common/admin/admin-top-product-point';
import { AdminProductStockPoint } from '../../common/admin/admin-product-stock-point';

import { API_ENDPOINTS } from '../../core/api.config';


@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private baseUrl = API_ENDPOINTS.ADMIN_DASHBOARD;

  constructor(private http: HttpClient) { }

  getDashboard(): Observable<AdminDashboardResponse> {
    return this.http.get<AdminDashboardResponse>(this.baseUrl);
    // Because interceptor is already working, you do not need headers here.
  }

  // Chat
  getOrdersPerDay() {
    return this.http.get<AdminOrdersPerDayPoint[]>(`${this.baseUrl}/orders-per-day`);
  }

  getRevenuePerDay() {
    return this.http.get<AdminRevenuePerDayPoint[]>(`${this.baseUrl}/revenue-per-day`);
  }

  getTopProducts() {
    return this.http.get<AdminTopProductPoint[]>(`${this.baseUrl}/top-products`);
  }

  getProductsByStock() {
    return this.http.get<AdminProductStockPoint[]>(`${this.baseUrl}/products-by-stock`);
  }




}
