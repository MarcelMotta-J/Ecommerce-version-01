import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Product as Prod } from '../common/product';
import { ProductCategory } from '../common/product-category';
import { API_ENDPOINTS } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  // API base URLs
  private baseUrl = API_ENDPOINTS.PRODUCTS;
  private categoryUrl = API_ENDPOINTS.PRODUCT_CATEGORY;

  constructor(private httpClient: HttpClient) { }

  /**
   * Get a single product by its ID
   * @param theProductId - Product ID to fetch
   * @returns Observable with the product data
   */
  getProduct(theProductId: number): Observable<Prod> {
    const productUrl = `${this.baseUrl}/${theProductId}`;

    return this.httpClient.get<Prod>(productUrl).pipe(
      catchError(this.handleError)  // Handle any errors
    );
  }

  /**
   * Get paginated products by category ID
   * @param thePage - Page number (0-based)
   * @param thePageSize - Number of items per page
   * @param theCategoryId - Category ID to filter by
   * @returns Observable with products and pagination info
   */
  getProductListPaginate(
    thePage: number,
    thePageSize: number,
    theCategoryId: number
  ): Observable<GetResponseProducts> {
    // Build URL with query parameters
    const searchUrl = `${this.baseUrl}/search/findByCategoryId?id=${theCategoryId}` +
      `&page=${thePage}&size=${thePageSize}`;

    return this.httpClient.get<GetResponseProducts>(searchUrl).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get all products for a category (non-paginated)
   * @param theCategoryId - Category ID
   * @returns Observable with array of products
   */
  getProductList(theCategoryId: number): Observable<Prod[]> {
    const searchUrl = `${this.baseUrl}/search/findByCategoryId?id=${theCategoryId}`;
    return this.getProducts(searchUrl);
  }

  /**
   * Search products by keyword (non-paginated)
   * @param theKeyword - Search term
   * @returns Observable with array of matching products
   */
  searchProducts(theKeyword: string): Observable<Prod[]> {
    const searchUrl = `${this.baseUrl}/search/findByNameContaining?name=${theKeyword}`;
    return this.getProducts(searchUrl);
  }

  /**
   * Helper method to extract products from API response
   * @param searchUrl - URL to fetch
   * @returns Observable with array of products
   */
  private getProducts(searchUrl: string): Observable<Prod[]> {
    return this.httpClient.get<GetResponseProducts>(searchUrl).pipe(
      // Extract products from the _embedded wrapper
      map(response => response._embedded?.products || []),
      catchError(this.handleError)
    );
  }

  /**
   * Search products with pagination
   * @param thePage - Page number (0-based)
   * @param thePageSize - Items per page
   * @param theKeyword - Search term
   * @returns Observable with products and pagination info
   */
  searchProductsPaginate(
    thePage: number,
    thePageSize: number,
    theKeyword: string
  ): Observable<GetResponseProducts> {
    const searchUrl = `${this.baseUrl}/search/findByNameContaining?name=${theKeyword}` +
      `&page=${thePage}&size=${thePageSize}`;

    return this.httpClient.get<GetResponseProducts>(searchUrl).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get all product categories
   * IMPORTANT: This is a PUBLIC endpoint - anyone can view categories
   * @returns Observable with array of product categories
   */
  getProductCategories(): Observable<ProductCategory[]> {
    const url = `${this.categoryUrl}?size=100&page=0`;

    return this.httpClient.get<GetResponseProductCategory>(url).pipe(
      map(response => response._embedded.productCategory),
      catchError(this.handleError)
    );
  }

  /**
   * Centralized error handler for all HTTP requests
   * @param error - The HTTP error response
   * @returns Observable that throws a user-friendly error
   */
  private handleError(error: HttpErrorResponse) {
    // Log the full error for debugging
    console.error('Product Service Error Details:', {
      status: error.status,
      statusText: error.statusText,
      message: error.message,
      error: error.error
    });

    let errorMessage = 'Unable to load products. Please try again.';

    // Handle different error types with user-friendly messages
    if (error.status === 0) {
      // Status 0 usually means network error or CORS issue
      errorMessage = 'Cannot connect to the application backend. Please verify that the services are running.';
    } else if (error.status === 401) {
      // Unauthorized - user not logged in
      errorMessage = 'Please log in to access this feature.';
    } else if (error.status === 403) {
      // Forbidden - logged in but not enough permissions
      errorMessage = 'You do not have permission to access this resource.';
    } else if (error.status === 404) {
      // Not found - endpoint or resource doesn't exist
      errorMessage = 'The requested resource was not found.';
    } else if (error.status === 500) {
      // Server error - backend exception
      errorMessage = 'Server error. Please try again later or contact support.';
    } else if (error.status === 503) {
      // Service unavailable - backend down
      errorMessage = 'Service temporarily unavailable. Please try again later.';
    }

    // Return an observable that throws the error message
    return throwError(() => new Error(errorMessage));
  }
}

/**
 * Response interface for product listing endpoints
 * Matches the structure returned by Spring Data REST
 */
interface GetResponseProducts {
  _embedded?: {           // Optional because error responses might not have it
    products: Prod[];     // Array of products
  };
  page: {                 // Pagination metadata
    size: number;         // Items per page
    totalElements: number; // Total number of items
    totalPages: number;    // Total number of pages
    number: number;        // Current page number
  };
}


interface GetResponseProductCategory {
  _embedded: {
    productCategory: ProductCategory[];
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}
