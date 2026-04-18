package com.mrcl.store1.admin.dto;

import java.time.LocalDate;

public record AdminOrdersPerDayPoint(
        LocalDate day,
        Long ordersCount
) {
}
