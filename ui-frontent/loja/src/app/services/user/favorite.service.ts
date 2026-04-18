import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';

import { Product } from '../../common/product';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class FavoriteService {
  // Backend endpoint for authenticated user favorites
  private baseUrl = API_ENDPOINTS.USER_FAVORITES;

  // In-memory cache of favorite product ids
  private favoriteIds = new Set<number>();

  // Reactive favorite counter
  private favoritesCountSubject = new BehaviorSubject<number>(0);

  // Public observable for components like header/sidebar
  favoritesCount$ = this.favoritesCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getFavorites(): Observable<Product[]> {
    // Returns the full list of favorite products
    return this.http.get<Product[]>(this.baseUrl);
  }

  loadFavorites(): void {
    // Loads favorites from backend and refreshes local state
    this.getFavorites().subscribe({
      next: (data: Product[]) => {
        this.favoriteIds = new Set<number>(
          data.map(product => Number(product.id))
        );
        this.emitFavoritesCount();
      },
      error: err => {
        console.error('Error loading favorites', err);
        this.favoriteIds = new Set<number>();
        this.emitFavoritesCount();
      }
    });
  }

  addFavorite(productId: number): Observable<void> {
    // Adds a product to favorites in backend and updates local state
    return this.http.post<void>(`${this.baseUrl}/${productId}`, {}).pipe(
      tap(() => {
        this.favoriteIds.add(Number(productId));
        this.emitFavoritesCount();
      })
    );
  }

  removeFavorite(productId: number): Observable<void> {
    // Removes a product from favorites in backend and updates local state
    return this.http.delete<void>(`${this.baseUrl}/${productId}`).pipe(
      tap(() => {
        this.favoriteIds.delete(Number(productId));
        this.emitFavoritesCount();
      })
    );
  }

  isFavorite(productId: number | string): boolean {
    // Checks whether a product is currently in the local favorite set
    return this.favoriteIds.has(Number(productId));
  }

  getFavoritesCount(): number {
    // Returns the current number of favorite products
    return this.favoriteIds.size;
  }

  clearFavorites(): void {
    // Clears local favorite state, useful on logout
    this.favoriteIds = new Set<number>();
    this.emitFavoritesCount();
  }

  private emitFavoritesCount(): void {
    // Pushes the current favorite count to observers
    this.favoritesCountSubject.next(this.favoriteIds.size);
  }
}