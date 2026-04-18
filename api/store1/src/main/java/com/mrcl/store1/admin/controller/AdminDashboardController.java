package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminDashboardResponse;
import com.mrcl.store1.admin.service.AdminDashboardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrcl.store1.admin.dto.AdminOrdersPerDayPoint;

import java.util.List;

import com.mrcl.store1.admin.dto.AdminRevenuePerDayPoint;

import com.mrcl.store1.admin.dto.AdminTopProductPoint;

import com.mrcl.store1.admin.dto.AdminProductStockPoint;


@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public AdminDashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/orders-per-day")
    public List<AdminOrdersPerDayPoint> getOrdersPerDay() {
        return dashboardService.getOrdersPerDay();
    }

    @GetMapping("/revenue-per-day")
    public List<AdminRevenuePerDayPoint> getRevenuePerDay() {
        return dashboardService.getRevenuePerDay();
    }

    @GetMapping("/top-products")
    public List<AdminTopProductPoint> getTopProducts() {
        return dashboardService.getTopProducts();
    }

    @GetMapping("/products-by-stock")
    public List<AdminProductStockPoint> getProductsByStock() {
        return dashboardService.getProductsByStock();
    }

}