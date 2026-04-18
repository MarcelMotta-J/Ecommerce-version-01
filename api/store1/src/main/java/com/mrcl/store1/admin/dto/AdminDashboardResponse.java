package com.mrcl.store1.admin.dto;

// for admin dashboard purposes
public record AdminDashboardResponse(
        long totalOrders,
        long pendingOrders,
        long totalProducts,
        long totalCustomers
) {}
