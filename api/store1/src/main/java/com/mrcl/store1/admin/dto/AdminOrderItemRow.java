package com.mrcl.store1.admin.dto;

import java.math.BigDecimal;

public record AdminOrderItemRow(
        Long id,
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        String imageUrl
) {
}
