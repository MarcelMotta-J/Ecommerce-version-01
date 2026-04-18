import { Component } from '@angular/core';

import { Router } from '@angular/router';

import { AdminCustomerRow } from '../../../common/admin/admin-customer-row';
import { AdminPagedResponse } from '../../../common/admin/admin-paged-response';
import { AdminCustomersService } from '../../../services/admin/admin-customers.service';

@Component({
  selector: 'app-admin-customers',
  standalone: false,
  templateUrl: './admin-customers.html',
  styleUrl: './admin-customers.css',
})
export class AdminCustomers {

  customers: AdminCustomerRow[] = [];

  page = 0;
  size = 10;

  totalElements = 0;
  totalPages = 0;

  loading = false;
  error = '';

  constructor(
    private adminCustomersService: AdminCustomersService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.error = '';

    this.adminCustomersService.getCustomers(this.page, this.size).subscribe({
      next: (response: AdminPagedResponse<AdminCustomerRow>) => {
        this.customers = response.content;
        this.page = response.page;
        this.size = response.size;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load customers.';
        this.loading = false;
      }
    });
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadCustomers();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadCustomers();
    }
  }

  editCustomer(customerId: number): void {
  this.router.navigate(['/admin/customers/edit', customerId]);
}

  deleteCustomer(customerId: number): void {
    const confirmed = window.confirm('Are you sure you want to delete this customer?');

    if (!confirmed) {
      return;
    }

    this.adminCustomersService.deleteCustomer(customerId).subscribe({
      next: () => {
        this.loadCustomers();
      },
      error: () => {
        this.error = 'Failed to delete customer.';
      }
    });
  }


}