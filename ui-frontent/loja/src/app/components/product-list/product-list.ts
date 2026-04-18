import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ProductService } from '../../services/product.service';
import {CartService } from '../../services/cart.service';
import { FavoriteService } from '../../services/user/favorite.service';
import { AuthService } from '../../services/auth.service';
import { SnackbarService } from '../../services/ui/snackbar.service';

import { Product } from '../../common/product';
import { CartItem } from '../../common/cart-item';

@Component({
  selector: 'app-product-list',
  standalone: false,
  templateUrl: './product-list-grid.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {

  // List of products currently displayed
  products: Product[] | null = [];

  // Category navigation state
  currentCategoryId: number = 1;
  previousCategoryId: number = 1;
  currentCategoryName: string | null = '';

  // Search mode flag
  searchMode: boolean = false;

  // Pagination state
  thePageNumber: number = 1;
  thePageSize: number = 5;
  theTotalElements: number = 0;

  // Stores the last search keyword to detect changes
  previousKeyword: string | null = '';

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private route: ActivatedRoute,
    private favoriteService: FavoriteService,
    public authService: AuthService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    // React whenever route parameters change
    this.route.paramMap.subscribe(() => {
      this.listProducts();
    });
  }

  listProducts(): void {
    // Decide whether current route is search mode or category mode
    this.searchMode = this.route.snapshot.paramMap.has('keyword');

    if (this.searchMode) {
      this.handleSearchProducts();
    } else {
      this.handleListProducts();
    }
  }

  handleSearchProducts(): void {
    const theKeyword: string = this.route.snapshot.paramMap.get('keyword')!;

    // Reset to page 1 when the search keyword changes
    if (this.previousKeyword !== theKeyword) {
      this.thePageNumber = 1;
    }

    this.previousKeyword = theKeyword;

    console.log(`keyword = ${theKeyword}, thePageNumber = ${this.thePageNumber}`);

    this.productService.searchProductsPaginate(
      this.thePageNumber - 1,
      this.thePageSize,
      theKeyword
    ).subscribe(this.processResult());
  }

  handleMissingImage(event: Event): void {
    // Hide broken images gracefully
    (event.target as HTMLImageElement).style.display = 'none';
  }

  handleListProducts(): void {
    this.products = [];
    this.currentCategoryName = '';

    // Check whether a category id is present in the route
    const hasCategoryId: boolean = this.route.snapshot.paramMap.has('id');

    if (hasCategoryId) {
      // Read category id from route
      this.currentCategoryId = +this.route.snapshot.paramMap.get('id')!;

      // Read category name safely, fallback to generic label
      this.currentCategoryName =
        this.route.snapshot.paramMap.get('name') ?? 'Products';
    } else {
      // Default category when no route param is given
      this.currentCategoryId = 1;
      this.currentCategoryName = 'Books';
    }

    // Reset to first page when category changes
    if (this.previousCategoryId !== this.currentCategoryId) {
      this.thePageNumber = 1;
    }

    this.previousCategoryId = this.currentCategoryId;

    console.log(
      `currentCategoryId = ${this.currentCategoryId}, thePageNumber = ${this.thePageNumber}`
    );

    this.productService.getProductListPaginate(
      this.thePageNumber - 1,
      this.thePageSize,
      this.currentCategoryId
    ).subscribe(this.processResult());
  }

  updatePageSize(pageSize: string): void {
    // Update page size and restart from page 1
    this.thePageSize = +pageSize;
    this.thePageNumber = 1;
    this.listProducts();
  }

  processResult() {
    // Process paginated response from backend
    return (data: any) => {
      this.products = data._embedded.products;
      this.thePageNumber = data.page.number + 1;
      this.thePageSize = data.page.size;
      this.theTotalElements = data.page.totalElements;
    };
  }

  addToCart(theProduct: Product): void {
    // Add product to cart and show feedback
    const theCartItem = new CartItem(theProduct);
    this.cartService.addToCart(theCartItem);

    this.snackbar.success('Added to cart');
  }

  isFavorite(productId: number | string): boolean {
    // Delegate favorite state check to the service
    return this.favoriteService.isFavorite(productId);
  }

  toggleFavorite(productId: number | string, event?: Event): void {
    // Prevent click from bubbling to parent link/card
    if (event) {
      event.stopPropagation();
    }

    const id = Number(productId);

    // Require login before allowing favorite action
    if (!this.authService.isLoggedIn()) {
      this.snackbar.error('Please login or register to use favorites.');
      return;
    }

    // Remove from favorites if already selected
    if (this.favoriteService.isFavorite(id)) {
      this.favoriteService.removeFavorite(id).subscribe({
        next: () => {
          this.snackbar.success('Removed from favorites');
        },
        error: err => {
          console.error('Error removing favorite', err);
          this.snackbar.error('Error removing favorite');
        }
      });
    } else {
      // Add to favorites if not selected yet
      this.favoriteService.addFavorite(id).subscribe({
        next: () => {
          this.snackbar.success('Added to favorites');
        },
        error: err => {
          console.error('Error adding favorite', err);
          this.snackbar.error('Error adding favorite');
        }
      });
    }
  }
}