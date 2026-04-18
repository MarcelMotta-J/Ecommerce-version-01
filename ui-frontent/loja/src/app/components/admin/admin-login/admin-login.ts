import { Component } from '@angular/core';

import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: false,
  templateUrl: './admin-login.html',
  styleUrl: './admin-login.css',
})
export class AdminLogin {

  email = '';
  password = '';

  loading = false;
  error = '';

  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  login(): void {
    this.loading = true;
    this.error = '';

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);

        if (!this.authService.isAdmin()) {
          this.authService.logout();
          this.error = 'This account does not have admin access.';
          this.loading = false;
          return;
        }

        this.router.navigate(['/admin/dashboard']);
      },
      error: () => {
        this.error = 'Invalid email or password';
        this.loading = false;
      }
    });
  }
}



