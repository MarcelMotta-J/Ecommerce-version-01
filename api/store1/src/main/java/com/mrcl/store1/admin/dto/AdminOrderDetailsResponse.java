package com.mrcl.store1.admin.dto;

import com.mrcl.store1.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AdminOrderDetailsResponse(
        Long id,
        String trackingNumber,
        OrderStatus status,
        BigDecimal totalPrice,
        int totalQuantity,
        Instant dateCreated,
        Instant dateUpdated,
        String userEmail,
        AdminCustomerRow customer,
        AdminAddressRow shippingAddress,
        AdminAddressRow billingAddress,
        List<AdminOrderItemRow> items
) {
}
