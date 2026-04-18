import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

import { API_ENDPOINTS } from '../core/api.config';

const publicEndpoints = [
  API_ENDPOINTS.AUTH_LOGIN,
  API_ENDPOINTS.AUTH_REGISTER,
  API_ENDPOINTS.PRODUCTS,
  API_ENDPOINTS.PRODUCT_CATEGORY,
  API_ENDPOINTS.COUNTRIES,
  API_ENDPOINTS.STATES
  
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const url = new URL(req.url, window.location.origin);
  const path = url.pathname;

  const isPublicEndpoint = publicEndpoints.some(endpoint =>
    path === endpoint || path.startsWith(`${endpoint}/`)
  );

  if (!token || isPublicEndpoint) {
    return next(req);
  }

  const cloned = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(cloned);
};