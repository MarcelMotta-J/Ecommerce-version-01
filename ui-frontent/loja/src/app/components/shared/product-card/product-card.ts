
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Product } from '../../../common/product';

@Component({
  selector: 'app-product-card',
  standalone: false,
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCardComponent {
  @Input() product!: Product;

  @Input() showFavoriteButton = false;
  @Input() favoriteActive = false;
  @Input() showAddToCart = true;
  @Input() showRemoveButton = false;

  @Output() addToCartClicked = new EventEmitter<Product>();
  @Output() favoriteToggled = new EventEmitter<Event>();
  @Output() removeClicked = new EventEmitter<number | string>();

  onAddToCart(): void {
    this.addToCartClicked.emit(this.product);
  }

  onToggleFavorite(event: Event): void {
    this.favoriteToggled.emit(event);
  }

  onRemove(): void {
    this.removeClicked.emit(this.product.id);
  }
}