import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { ApiErrorResponse } from '../../../common/api-error-response';


@Component({
  selector: 'app-register.component',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  generalError = '';
  successMessage = '';
  fieldErrors: Record<string, string> = {};
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phoneNumber: ['']
    });

    // clean error whe user starts to type
    this.registerForm.valueChanges.subscribe(() => {
    this.generalError = '';
  });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.clearMessages();

    const payload = this.registerForm.getRawValue();

    this.authService.register(payload).subscribe({
      next: () => {
        this.successMessage = 'Conta criada com sucesso. Faça login para continuar.';
        this.loading = false;

        this.registerForm.reset({
          email: '',
          password: '',
          firstName: '',
          lastName: '',
          phoneNumber: ''
        });

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1200);
      },
      error: (err: HttpErrorResponse) => {
        this.handleApiError(err);
        this.loading = false;
      }
    });
  }

  private handleApiError(err: HttpErrorResponse): void {
    const apiError = err.error as ApiErrorResponse;

    this.fieldErrors = apiError?.fieldErrors ?? {};

    if (apiError?.message) {
      this.generalError = apiError.message;
    } else if (err.status === 0) {
      this.generalError = 'Não foi possível conectar ao servidor.';
    } else {
      this.generalError = 'Ocorreu um erro inesperado.';
    }
  }

  // used in onSubmit
  private clearMessages(): void {
    this.generalError = '';
    this.successMessage = '';
    this.fieldErrors = {};
  }

  clearGeneralError(): void {
    this.generalError = '';
  }

  get email() {
    return this.registerForm.get('email');
  }

  get password() {
    return this.registerForm.get('password');
  }

  get firstName() {
    return this.registerForm.get('firstName');
  }

  get lastName() {
    return this.registerForm.get('lastName');
  }

  get phoneNumber() {
    return this.registerForm.get('phoneNumber');
  }
}