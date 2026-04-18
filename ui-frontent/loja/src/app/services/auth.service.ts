import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { LoginRequest } from '../common/auth/login-request';
import { LoginResponse } from '../common/auth/login-response';
import { RegisterRequest } from '../common/auth/register-request';
import { API_ENDPOINTS } from '../core/api.config';

interface JwtPayload {
  sub?: string;
  roles?: string[];
  exp?: number;
  iat?: number;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly tokenKey = 'token';

  private loginUrl = API_ENDPOINTS.AUTH_LOGIN;
  private registerUrl = API_ENDPOINTS.AUTH_REGISTER;

  constructor(private http: HttpClient) { }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, request);
  }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(this.registerUrl, request);
  }

  saveToken(token: string): void {
    //localStorage.setItem('token', token);
    localStorage.setItem(this.tokenKey, token);

  }

  getToken(): string | null {
    //return localStorage.getItem('token');
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    //localStorage.removeItem('token');
     localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const payload = this.getTokenPayload();

    if (!payload?.exp) {
      return false;
    }

    const nowInSeconds = Math.floor(Date.now() / 1000);
    return payload.exp > nowInSeconds;
  }

  getUserEmail(): string | null {
    return this.getTokenPayload()?.sub ?? null;
  }

  getRoles(): string[] {
    return this.getTokenPayload()?.roles ?? [];
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }

  /*
  private getTokenPayload(): JwtPayload | null {
    const token = this.getToken();

    if (!token) {
      return null;
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    try {
      return JSON.parse(atob(parts[1]));
    } catch {
      return null;
    }
  }
    */


  private getTokenPayload(): JwtPayload | null {
    const token = this.getToken();

    if (!token) {
      return null;
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    try {
      return JSON.parse(this.decodeBase64Url(parts[1]));
    } catch {
      return null;
    }
  }

  private decodeBase64Url(value: string): string {
    const base64 = value.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
    return atob(padded);
  }

}