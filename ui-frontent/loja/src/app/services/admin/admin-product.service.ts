import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AdminPagedResponse } from '../../common/admin/admin-paged-response';
import { AdminProductRow } from '../../common/admin/admin-product-row';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class AdminProductService {
  private baseUrl = API_ENDPOINTS.ADMIN_PRODUCTS;

  constructor(private http: HttpClient) { }

  getProducts(
    page: number,
    size: number,
    categoryId?: number | null,
    active?: boolean | null,
    name?: string | null
  ): Observable<AdminPagedResponse<AdminProductRow>> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (categoryId !== null && categoryId !== undefined) {
      params = params.set('categoryId', categoryId);
    }

    if (active !== null && active !== undefined) {
      params = params.set('active', active);
    }

    if (name && name.trim()) {
      params = params.set('name', name.trim());
    }

    return this.http.get<AdminPagedResponse<AdminProductRow>>(this.baseUrl, { params });
  }

  updateActive(productId: number, active: boolean): Observable<void> {
    return this.http.patch<void>(
      `${this.baseUrl}/${productId}/active`,
      {},
      {
        params: { active }
      }
    );
  }

  updateStock(productId: number, unitsInStock: number): Observable<void> {
    return this.http.patch<void>(
      `${this.baseUrl}/${productId}/stock`,
      {},
      {
        params: { unitsInStock }
      }
    );
  }

 
}


