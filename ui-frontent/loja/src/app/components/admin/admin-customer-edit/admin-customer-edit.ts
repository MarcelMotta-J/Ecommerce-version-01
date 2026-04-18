

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminCustomersService } from '../../../services/admin/admin-customers.service';
import { AdminCustomerRow } from '../../../common/admin/admin-customer-row';

@Component({
  selector: 'app-admin-customer-edit',
  standalone: false,
  templateUrl: './admin-customer-edit.html',
  styleUrl: './admin-customer-edit.css',
})
export class AdminCustomerEdit implements OnInit {
  customerId!: number;

  firstName = '';
  lastName = '';
  email = '';

  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminCustomersService: AdminCustomersService
  ) {}

  ngOnInit(): void {
    this.customerId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCustomer();
  }

  loadCustomer(): void {
    this.loading = true;
    this.error = '';

    this.adminCustomersService.getCustomerById(this.customerId).subscribe({
      next: (customer: AdminCustomerRow) => {
        this.firstName = customer.firstName;
        this.lastName = customer.lastName;
        this.email = customer.email;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load customer.';
        this.loading = false;
      }
    });
  }

  saveCustomer(): void {
    const customer = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email
    };

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    this.adminCustomersService.updateCustomer(this.customerId, customer).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Customer updated successfully.';
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to update customer.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/customers']);
  }
}