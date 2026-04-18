package com.mrcl.store1.auth.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record UserOrderDetailResponse(
        Long id,
        String trackingNumber,
        String status,
        BigDecimal totalPrice,
        int totalQuantity,
        Instant dateCreated,
        Instant dateUpdated,
        List<OrderItemDetail> items,
        AddressDetail shippingAddress,
        AddressDetail billingAddress
) {
}
