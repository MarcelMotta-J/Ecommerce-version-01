package com.mrcl.store1.auth.dto;

import java.math.BigDecimal;

public record OrderItemDetail(
        Long productId,
        String productName,
        String imageUrl,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
