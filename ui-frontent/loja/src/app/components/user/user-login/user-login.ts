import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../../services/auth.service';
import { FavoriteService } from '../../../services/user/favorite.service';
import { RealtimeNotificationService } from '../../../services/user/realtime-notification';
import { NotificationService } from '../../../services/user/notification.service';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.html',
  styleUrls: ['./user-login.css'],
  standalone: false
})
export class UserLogin {

  // Reactive form for login fields
  loginForm: FormGroup;

  // UI state flags
  loading = false;
  error = '';
  hidePassword = true;
  showDemo = true;
  showSocialLogin = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private favoriteService: FavoriteService,
    private router: Router,
    private route: ActivatedRoute,
    private realtimeNotificationService: RealtimeNotificationService,
    private notificationService: NotificationService
  ) {
    // Build the login form
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });

    // Restore remembered email if it exists in localStorage
    const savedEmail = localStorage.getItem('rememberedEmail');
    if (savedEmail) {
      this.loginForm.patchValue({
        email: savedEmail,
        rememberMe: true
      });
    }
  }

  onSubmit(): void {
    // Stop submission if form is invalid
    if (this.loginForm.invalid) {
      Object.keys(this.loginForm.controls).forEach(key => {
        this.loginForm.get(key)?.markAsTouched();
      });
      return;
    }

    // Reset UI state before sending login request
    this.loading = true;
    this.error = '';

    const { email, password, rememberMe } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: (response: { token: string }) => {
        // Save JWT token first
        this.authService.saveToken(response.token);

        // Load user favorites after successful login
        this.favoriteService.loadFavorites();

        this.notificationService.loadLatestNotifications();
        this.realtimeNotificationService.connect();

        // Persist remembered email only if requested
        if (rememberMe) {
          localStorage.setItem('rememberedEmail', email);
        } else {
          localStorage.removeItem('rememberedEmail');
        }

        // Read optional returnUrl from query params
        // This is useful when a protected page redirected the user to login
        const returnUrl = this.route.snapshot.queryParams['returnUrl'];
        console.log('returnUrl =', returnUrl);
        // Redirect admin users to admin dashboard
        if (this.authService.isAdmin()) {
          this.router.navigate(['/admin/dashboard']);
        }
        // Redirect normal users back to the page they originally wanted
        else if (returnUrl) {
          this.router.navigateByUrl(returnUrl);
        }
        // Fallback route if no returnUrl exists
        else {
          this.router.navigate(['/products']);
        }

        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        // Show backend error message when available
        this.error = err.error?.message || err.message || 'Invalid email or password';
        this.loading = false;
      }
    });
  }

  fillDemoCredentials(email: string, password: string): void {
    // Quickly fill the form with demo credentials
    this.loginForm.patchValue({
      email,
      password
    });
  }
}