package com.mrcl.store1.admin.dto;

import com.mrcl.store1.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO returned to admin UI.
 * We do NOT return full JPA entities to avoid lazy loading problems + leaking internal fields.
 */
public record AdminOrderRow(
        Long id,
        String trackingNumber,
        String userEmail,      // from AppUser (nullable)
        String customerEmail,  // from Customer (nullable)
        OrderStatus status,
        BigDecimal totalPrice,
        int totalQuantity,
        Instant dateCreated
) {}
