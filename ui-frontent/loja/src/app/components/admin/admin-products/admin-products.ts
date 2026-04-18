import { Component, OnInit } from '@angular/core';



import { AdminProductRow } from '../../../common/admin/admin-product-row';
import { AdminProductService } from '../../../services/admin/admin-product.service';

@Component({
  selector: 'app-admin-products',
  standalone: false,
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css',
})
export class AdminProducts implements OnInit{
  
  products: AdminProductRow[] = [];

  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  categoryId: number | null = null;
  active: boolean | null = null;
  name = '';

  loading = false;
  error = '';

  constructor(private adminProductService: AdminProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = '';

    this.adminProductService
      .getProducts(this.page, this.size, this.categoryId, this.active, this.name)
      .subscribe({
        next: (response) => {
          this.products = response.content;
          this.page = response.page;
          this.size = response.size;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load admin products.';
          this.loading = false;
        }
      });
  }

  search(): void {
    this.page = 0;
    this.loadProducts();
  }

  clearFilters(): void {
    this.page = 0;
    this.categoryId = null;
    this.active = null;
    this.name = '';
    this.loadProducts();
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadProducts();
    }
  }

  toggleActive(product: AdminProductRow): void {
    this.adminProductService.updateActive(product.id, !product.active).subscribe({
      next: () => this.loadProducts(),
      error: () => {
        this.error = 'Failed to update product status.';
      }
    });
  }

  updateStock(product: AdminProductRow, event: Event): void {
    const input = event.target as HTMLInputElement;
    const newStock = Number(input.value);

    if (Number.isNaN(newStock)) {
      return;
    }

    this.adminProductService.updateStock(product.id, newStock).subscribe({
      next: () => this.loadProducts(),
      error: () => {
        this.error = 'Failed to update stock.';
      }
    });
  }
}
  


