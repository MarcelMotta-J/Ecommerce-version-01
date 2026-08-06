package com.mrcl.store1.entity;

/**
 * Order lifecycle:
 * PENDING  -> created, waiting payment
 * PAID     -> payment confirmed
 * SHIPPED  -> shipped to customer
 * CANCELLED-> cancelled (by admin or customer rules)
 * REFUNDED -> total refunding to buyer
 * CHARGEBACK -> under judge
 */
public enum OrderStatus {
    PENDING,
    PAID,
    PAYMENT_FAILED,
    SHIPPED,
    CANCELLED,
    REFUNDED,
    CHARGEBACK
}