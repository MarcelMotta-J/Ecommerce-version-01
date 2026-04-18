package com.mrcl.store1.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AdminRevenuePerDayView {
    LocalDate getDay();
    BigDecimal getRevenue();
}
