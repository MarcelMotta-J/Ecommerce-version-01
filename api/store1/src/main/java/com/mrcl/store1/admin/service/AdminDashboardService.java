package com.mrcl.store1.admin.service;


import com.mrcl.store1.admin.dto.AdminDashboardResponse;
import com.mrcl.store1.dao.CustomerRepository;
import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.stereotype.Service;

import com.mrcl.store1.admin.dto.AdminRevenuePerDayPoint;
import com.mrcl.store1.admin.dto.AdminRevenuePerDayView;

import com.mrcl.store1.admin.dto.AdminOrdersPerDayPoint;

import java.time.LocalDate;
import java.util.List;

import com.mrcl.store1.admin.dto.AdminTopProductPoint;
import com.mrcl.store1.admin.dto.AdminTopProductView;

import com.mrcl.store1.admin.dto.AdminProductStockPoint;
import com.mrcl.store1.admin.dto.AdminProductStockView;

@Service
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public AdminDashboardService(OrderRepository orderRepository,
                                 ProductRepository productRepository,
                                 CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public AdminDashboardResponse getDashboard() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long totalProducts = productRepository.count();
        long totalCustomers = customerRepository.count();

        return new AdminDashboardResponse(
                totalOrders,
                pendingOrders,
                totalProducts,
                totalCustomers
        );
    }

    public List<AdminOrdersPerDayPoint> getOrdersPerDay() {
        return orderRepository.findOrdersPerDay()
                .stream()
                .map(row -> new AdminOrdersPerDayPoint(
                        row.getDay(),
                        row.getOrdersCount()
                ))
                .toList();
    }

    public List<AdminRevenuePerDayPoint> getRevenuePerDay() {
        return orderRepository.findRevenuePerDay()
                .stream()
                .map(row -> new AdminRevenuePerDayPoint(
                        row.getDay(),
                        row.getRevenue()
                ))
                .toList();
    }

    public List<AdminTopProductPoint> getTopProducts() {
        return orderRepository.findTopProducts()
                .stream()
                .map(row -> new AdminTopProductPoint(
                        row.getName(),
                        row.getTotalSold()
                ))
                .toList();
    }

    public List<AdminProductStockPoint> getProductsByStock() {
        return productRepository.findTopProductsByStock()
                .stream()
                .map(row -> new AdminProductStockPoint(
                        row.getName(),
                        row.getUnitsInStock()
                ))
                .toList();
    }

}