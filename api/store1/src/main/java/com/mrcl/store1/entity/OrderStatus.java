package com.mrcl.store1.entity;

/**
 * Order lifecycle:
 * PENDING  -> created, waiting payment
 * PAID     -> payment confirmed
 * SHIPPED  -> shipped to customer
 * CANCELLED-> cancelled (by admin or customer rules)
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    CANCELLED
}