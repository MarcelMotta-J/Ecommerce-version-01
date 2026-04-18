package com.mrcl.store1.auth.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record UserOrderResponse(
        Long id,
        String trackingNumber,
        String status,
        BigDecimal totalPrice,   // ← 4
        int totalQuantity,       // ← 5
        Instant dateCreated,
        int itemCount
) {
}
