import { Component, OnInit  } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';

interface OrderDetail {
  id: number;
  trackingNumber: string;
  status: string;
  totalPrice: number;
  totalQuantity: number;
  dateCreated: string;
  dateUpdated: string;
  items: Array<{
    productId: number;
    productName: string;
    imageUrl: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
  }>;
  shippingAddress: {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
  };
  billingAddress: {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
  };
}

@Component({
  selector: 'app-user-order-detail',
  standalone: false,
  templateUrl: './user-order-detail.html',
  styleUrl: './user-order-detail.css',
})
export class UserOrderDetail implements OnInit{

  orderId: number;
  order: any = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.orderId = +this.route.snapshot.paramMap.get('id')!;
  }

  ngOnInit(): void {
    this.loadOrderDetails();
  }


  loadOrderDetails(): void {
    this.loading = true;
    this.http.get<OrderDetail>(`/api/user/orders/${this.orderId}`).subscribe({
      next: (data) => {
        this.order = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load order details';
        this.loading = false;
        this.snackBar.open('Error loading order details', 'Close', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  handleMissingImage(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/images/placeholder.png';
  }


}
