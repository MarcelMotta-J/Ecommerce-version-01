package com.mrcl.store1.admin.dto;

import java.time.LocalDate;

public interface AdminOrdersPerDayView {
    LocalDate getDay();
    Long getOrdersCount();
}

