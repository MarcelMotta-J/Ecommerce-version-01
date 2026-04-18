import { Component, OnInit } from '@angular/core';

import { AdminOrderRow } from '../../../common/admin/admin-order-row';
import { AdminOrderService } from '../../../services/admin/admin-order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-orders',
  standalone: false,
  templateUrl: './admin-orders.html',
  styleUrl: './admin-orders.css',
})
export class AdminOrders implements OnInit {

  orders: AdminOrderRow[] = [];

  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  status: string | null = null;
  customerEmail = '';

  loading = false;
  error = '';

  constructor(
    private adminOrderService: AdminOrderService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.error = '';

    this.adminOrderService
      .getOrders(this.page, this.size, this.status, this.customerEmail)
      .subscribe({
        next: (response) => {
          this.orders = response.content;
          this.page = response.page;
          this.size = response.size;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load admin orders.';
          this.loading = false;
        }
      });
  }

  search(): void {
    this.page = 0;
    this.loadOrders();
  }

  clearFilters(): void {
    this.page = 0;
    this.status = null;
    this.customerEmail = '';
    this.loadOrders();
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadOrders();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadOrders();
    }
  }

  viewOrder(orderId: number): void {
    this.router.navigate(['/admin/orders', orderId]);
  }

}
