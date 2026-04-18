import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminOrderService } from '../../../services/admin/admin-order.service';
import { AdminOrderDetailsResponse } from '../../../common/admin/AdminOrderDetailsResponse';


@Component({
  selector: 'app-admin-order-details',
  standalone: false,
  templateUrl: './admin-order-details.html',
  styleUrl: './admin-order-details.css',
})
export class AdminOrderDetails implements OnInit {

  selectedStatus = '';
  successMessage = '';

  order?: AdminOrderDetailsResponse;

  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private adminOrderService: AdminOrderService,
    private router: Router,
  ) { }

  ngOnInit(): void {

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error = 'Invalid order id';
      return;
    }

    this.loading = true;

    this.adminOrderService.getOrderDetails(id).subscribe({
      next: (data) => {
        this.order = data;
        this.selectedStatus = data.status;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load order details';
        this.loading = false;
      }
    });

  }

  updateStatus(): void {
    if (!this.order || !this.selectedStatus) {
      return;
    }

    this.successMessage = '';
    this.error = '';

    this.adminOrderService.updateOrderStatus(this.order.id, this.selectedStatus).subscribe({
      next: () => {
        if (this.order) {
          this.order.status = this.selectedStatus;
        }
        this.successMessage = 'Order status updated successfully.';
      },
      error: () => {
        this.error = 'Failed to update order status.';
      }
    });
  }


  goBackToOrders(): void {
    this.router.navigate(['/admin/orders']);
  }

}
