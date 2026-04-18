package com.mrcl.store1.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminRevenuePerDayPoint(
        LocalDate day,
        BigDecimal revenue
) {
}
