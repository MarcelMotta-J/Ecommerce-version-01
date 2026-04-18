import { Component, OnInit } from '@angular/core';
import {CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-status',
  standalone: false,
  templateUrl: './cart-status.html',
  styleUrl: './cart-status.css',
})
export class CartStatus implements OnInit {

  totalPrice: number = 0.00;

  totalQuantity: number = 0;

  constructor(private cartService: CartService) { }

  ngOnInit(): void {
    this.updateCartStatus()
  }

  updateCartStatus() {
    // subscribe to the totalPrice
    this.cartService.totalPrice.subscribe(
      data => this.totalPrice = data
    );

    // subscribe to the totalQuantity
    this.cartService.totalQuantity.subscribe(
      data => this.totalQuantity = data
    );
  }




}
