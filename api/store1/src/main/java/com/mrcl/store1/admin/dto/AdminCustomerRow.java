package com.mrcl.store1.admin.dto;

import java.time.Instant;

public record AdminCustomerRow(
        Long id,
        String firstName,
        String lastName,
        String email,
        Long ordersCount

) {
}
