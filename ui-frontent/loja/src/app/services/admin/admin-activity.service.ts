import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AdminActionLogRow } from '../../common/admin/admin-action-log-row';

import { AdminPagedResponse } from '../../common/admin/admin-paged-response';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class AdminActivityService {

  private baseUrl = API_ENDPOINTS.ADMIN_ACTIVITY;

  constructor(private http: HttpClient) { }

  getActivity(page: number, size: number): Observable<AdminPagedResponse<AdminActionLogRow>> {
    return this.http.get<AdminPagedResponse<AdminActionLogRow>>(this.baseUrl, {
      params: {
        page,
        size
      }
    });
  }


  exportActivityTxt() {
    return this.http.get('/api/admin/activity/export/txt', {
      responseType: 'blob'
    });
  }

  exportActivityPdf() {
    return this.http.get(`${this.baseUrl}/export/pdf`, {
      responseType: 'blob'
    });
  }



}
