package com.mrcl.store1.admin.service;


import com.mrcl.store1.admin.dto.*;
import com.mrcl.store1.dao.CustomerRepository;
import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mrcl.store1.entity.Order;

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


    @Transactional(readOnly = true)
    public AdminPagedResponse<AdminOrderRow> listOrders(
            int page,
            int size,
            OrderStatus status,
            String customerEmail
    ) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        boolean hasStatus = status != null;
        boolean hasCustomerEmail =
                customerEmail != null && !customerEmail.isBlank();

        Page<Order> result;

        if (hasStatus && hasCustomerEmail) {

            result = orderRepository
                    .findByStatusAndCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(
                            status,
                            customerEmail,
                            pageable
                    );

        } else if (hasStatus) {

            result = orderRepository
                    .findByStatusOrderByDateCreatedDesc(
                            status,
                            pageable
                    );

        } else if (hasCustomerEmail) {

            result = orderRepository
                    .findByCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(
                            customerEmail,
                            pageable
                    );

        } else {

            result = orderRepository
                    .findAllByOrderByDateCreatedDesc(
                            pageable
                    );
        }

        return new AdminPagedResponse<>(
                result.getContent()
                        .stream()
                        .map(order -> new AdminOrderRow(
                                order.getId(),
                                order.getOrderTrackingNumber(),
                                order.getAppUser() != null
                                        ? order.getAppUser().getEmail()
                                        : null,
                                order.getCustomer() != null
                                        ? order.getCustomer().getEmail()
                                        : null,
                                order.getStatus(),
                                order.getTotalPrice(),
                                order.getTotalQuantity(),
                                order.getDateCreated()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }


    @Transactional(readOnly = true)
    public AdminOrderDetailsResponse getOrderDetails(Long id) {
        // código movido do AdminOrderController
        Order o = orderRepository.findDetailedById(id).orElseThrow();

        String userEmail = (o.getAppUser() != null) ? o.getAppUser().getEmail() : null;

        AdminCustomerRow customer = null;
        if (o.getCustomer() != null) {
            customer = new AdminCustomerRow(
                    o.getCustomer().getId(),
                    o.getCustomer().getFirstName(),
                    o.getCustomer().getLastName(),
                    o.getCustomer().getEmail(),
                    (long) o.getCustomer().getOrders().size()
            );
        }

        AdminAddressRow shippingAddress = null;
        if (o.getShippingAddress() != null) {
            shippingAddress = new AdminAddressRow(
                    o.getShippingAddress().getId(),
                    o.getShippingAddress().getStreet(),
                    o.getShippingAddress().getCity(),
                    o.getShippingAddress().getState(),
                    o.getShippingAddress().getCountry(),
                    o.getShippingAddress().getZipCode()
            );
        }

        AdminAddressRow billingAddress = null;
        if (o.getBillingAddress() != null) {
            billingAddress = new AdminAddressRow(
                    o.getBillingAddress().getId(),
                    o.getBillingAddress().getStreet(),
                    o.getBillingAddress().getCity(),
                    o.getBillingAddress().getState(),
                    o.getBillingAddress().getCountry(),
                    o.getBillingAddress().getZipCode()
            );
        }

        List<AdminOrderItemRow> items = o.getOrderItems().stream()
                .map(i -> new AdminOrderItemRow(
                        i.getId(),
                        i.getProductId(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getImageUrl()
                ))
                .toList();

        return new AdminOrderDetailsResponse(
                o.getId(),
                o.getOrderTrackingNumber(),
                o.getStatus(),
                o.getTotalPrice(),
                o.getTotalQuantity(),
                o.getDateCreated(),
                o.getDateUpdated(),
                userEmail,
                customer,
                shippingAddress,
                billingAddress,
                items
        );
    }

}