import { Component, OnInit } from '@angular/core';

import { FavoriteService } from '../../../services/user/favorite.service';
import {CartService } from '../../../services/cart.service';
import { SnackbarService } from '../../../services/ui/snackbar.service';

import { Product } from '../../../common/product';
import { CartItem } from '../../../common/cart-item';

@Component({
  selector: 'app-favorite.component',
  standalone: false,
  templateUrl: './favorite.component.html',
  styleUrl: './favorite.component.css',
})
export class FavoriteComponent implements OnInit {

  // List of favorite products shown on screen
  favoriteProducts: Product[] = [];

  // UI state flags
  loading = false;
  error = '';

  constructor(
    private favoriteService: FavoriteService,
    private cartService: CartService,
    private snackbar: SnackbarService
  ) {}

  ngOnInit(): void {
    // Load favorites when the page opens
    this.loadFavorites();
  }

  loadFavorites(): void {
    // Reset UI state before loading
    this.loading = true;
    this.error = '';

    this.favoriteService.getFavorites().subscribe({
      next: (data: Product[]) => {
        this.favoriteProducts = data;
        this.loading = false;
      },
      error: err => {
        console.error('Error loading favorites', err);
        this.error = 'Could not load your favorite products.';
        this.loading = false;
      }
    });
  }

  addToCart(product: Product): void {
    // Add selected product to cart
    const cartItem = new CartItem(product);
    this.cartService.addToCart(cartItem);

    // Show success feedback
    this.snackbar.success('Added to cart');
  }

  removeFavorite(productId: number | string): void {
    const id = Number(productId);

    this.favoriteService.removeFavorite(id).subscribe({
      next: () => {
        // Update the local list immediately for better UX
        this.favoriteProducts = this.favoriteProducts.filter(
          product => Number(product.id) !== id
        );

        this.snackbar.success('Removed from favorites');
      },
      error: err => {
        console.error('Error removing favorite', err);
        this.snackbar.error('Error removing favorite');
      }
    });
  }
}