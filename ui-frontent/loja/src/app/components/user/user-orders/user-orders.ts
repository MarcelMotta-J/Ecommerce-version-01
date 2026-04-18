import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserOrderResponse } from '../../../common/user/user-order-response';
import { UserOrderPageResponse } from '../../../common/user/user-order-page-response';
import { UserAddressResponse } from '../../../common/user/user-address-response';


@Component({
  selector: 'app-user-orders',
  standalone: false,
  templateUrl: './user-orders.html',
  styleUrl: './user-orders.css',
})
export class UserOrders implements OnInit {

  orders: UserOrderResponse[] = [];
  loading = false;
  error = '';

  currentPage = 0;
  totalPages = 0;
  pageSize = 10;
  totalElements = 0;


  constructor(
    private http: HttpClient,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.error = '';

    this.http.get<UserOrderPageResponse<UserOrderResponse>>(
      `/api/user/orders?page=${this.currentPage}&size=${this.pageSize}`
    ).subscribe({
      next: (data) => {
        this.orders = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading orders:', err);
        this.error = 'Failed to load orders';
        this.loading = false;
        this.snackBar.open('Error loading orders', 'Close', { duration: 3000 });
      }
    });
  }

  viewOrderDetails(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'PAID': return 'success';
      case 'SHIPPED': return 'info';
      case 'CANCELLED': return 'danger';
      default: return 'default';
    }
  }

  goToNextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadOrders();
    }
  }

  goToPreviousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadOrders();
    }
  }

}
