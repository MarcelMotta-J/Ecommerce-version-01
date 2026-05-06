import { Component, OnInit } from '@angular/core';
import { Product } from '../../common/product';
import { ProductService } from '../../services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartItem } from '../../common/cart-item';

import { ReviewService } from '../../services/user/review.service';
import { Review } from '../../common/user/review';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-product-details',
  standalone: false,
  templateUrl: './product-details.html',
  styleUrl: './product-details.css',
})
export class ProductDetails implements OnInit {


  product: Product = new Product();

  reviews: Review[] = [];

  //newRating: number = 5;
  newComment: string = '';
  averageRating: number = 0;

  newRating: number = 5;
  hoverRating: number = 0;

  hasUserReviewed: boolean = false;

  userReview: Review | null = null;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private reviewService: ReviewService,
    public authService: AuthService
  ) { }


  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleProductDetails();
    });
  }

  handleProductDetails() {

    // get the "id" param string. Convert to a number using the "+" symbol
    const theProductId: number = +this.route.snapshot.paramMap.get('id')!;

    this.productService.getProduct(theProductId).subscribe(
      data => {
        this.product = data;
      }
    );

    this.loadReviews(theProductId);

  }

  addToCart() {
    console.log(`Adding to cart: ${this.product.name},  ${this.product.unitPrice}`);

    // TODO ... do the real work
    const theCartItem = new CartItem(this.product);

    this.cartService.addToCart(theCartItem);
  }

  calculateAverageRating(): void {
    if (this.reviews.length === 0) {
      this.averageRating = 0;
      return;
    }

    const total = this.reviews.reduce((sum, review) => sum + review.rating, 0);
    this.averageRating = total / this.reviews.length;
  }

  submitReview(): void {
    const productId = Number(this.product.id);

    const review = {
      productId: productId,
      rating: Number(this.newRating),
      comment: this.newComment
    };

    this.reviewService.createReview(review).subscribe({
      next: () => {
        this.newComment = '';
        this.newRating = 5;
        this.loadReviews(productId);
      },
      error: err => {
        console.error('Error creating review', err);
        alert('Erro ao enviar avaliação.');
      }
    });
  }

  loadReviews(productId: number): void {
    this.reviewService.getReviews(productId).subscribe(data => {
      this.reviews = data;
      this.calculateAverageRating();
    });

    this.hasUserReviewed = this.reviews.some(
      r => r.userEmail === this.authService.getUserEmail()
    );

    this.userReview = this.reviews.find(
      r => r.userEmail === this.authService.getUserEmail()
    ) || null;

    if (this.userReview) {
      this.newRating = this.userReview.rating;
      this.newComment = this.userReview.comment;
    }


  }

  setRating(rating: number): void {
    this.newRating = rating;
  }

  setHover(rating: number): void {
    this.hoverRating = rating;
  }

  clearHover(): void {
    this.hoverRating = 0;
  }

  getDisplayRating(): number {
    return this.hoverRating || this.newRating;
  }


  getInitials(email: string | null): string {
    if (!email) return '?';

    const name = email.split('@')[0];

    return name
      .split(/[._-]/)
      .filter(part => part.length > 0)
      .map(part => part[0].toUpperCase())
      .slice(0, 2)
      .join('');
  }

}
