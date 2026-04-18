package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminOrderDetailsResponse;
import com.mrcl.store1.admin.dto.AdminOrderItemRow;
import com.mrcl.store1.admin.dto.AdminOrderRow;
import com.mrcl.store1.admin.dto.AdminCustomerRow;
import com.mrcl.store1.admin.dto.AdminAddressRow;
import com.mrcl.store1.admin.dto.UpdateOrderStatusRequest;
import com.mrcl.store1.admin.dto.AdminPagedResponse;
import com.mrcl.store1.admin.service.AdminActionLogService;
import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;

import java.util.List;



@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepo;

    private final AdminActionLogService logService;

    public AdminOrderController(OrderRepository orderRepo, AdminActionLogService logService) {
        this.orderRepo = orderRepo;
        this.logService = logService;
    }

    /**
     * ✅ List orders for admin dashboard.
     * Optional filter by status: /api/admin/orders?status=PENDING
     */
    @GetMapping
    public AdminPagedResponse<AdminOrderRow> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerEmail
    ) {
        Pageable pageable = PageRequest.of(page, size);

        boolean hasStatus = status != null;
        boolean hasCustomerEmail = customerEmail != null && !customerEmail.isBlank();

        Page<Order> result;

        if (hasStatus && hasCustomerEmail) {
            result = orderRepo.findByStatusAndCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(
                    status, customerEmail, pageable
            );
        } else if (hasStatus) {
            result = orderRepo.findByStatusOrderByDateCreatedDesc(status, pageable);
        } else if (hasCustomerEmail) {
            result = orderRepo.findByCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(customerEmail, pageable);
        } else {
            result = orderRepo.findAllByOrderByDateCreatedDesc(pageable);
        }

        return new AdminPagedResponse<>(
                result.getContent().stream().map(o -> new AdminOrderRow(
                        o.getId(),
                        o.getOrderTrackingNumber(),
                        o.getAppUser() != null ? o.getAppUser().getEmail() : null,
                        o.getCustomer() != null ? o.getCustomer().getEmail() : null,
                        o.getStatus(),
                        o.getTotalPrice(),
                        o.getTotalQuantity(),
                        o.getDateCreated()
                )).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    /**
     * ✅ Change order status (admin)
     * Example:
     * PATCH /api/admin/orders/10/status
     * body: {"status":"PAID"}
     */
    @PatchMapping("/{id}/status")
    public void updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest req,
            Authentication auth
    ) {

        if (req == null || req.status() == null) {
            throw new IllegalArgumentException("status is required");
        }

        Order order = orderRepo.findById(id).orElseThrow();

        OrderStatus newStatus = OrderStatus.valueOf(req.status());
        order.setStatus(newStatus);

        orderRepo.save(order);

        logService.log(auth, "Updated order #" + id + " status to " + newStatus);
    }

    @GetMapping("/{id}")
    public AdminOrderDetailsResponse getOrderDetails(@PathVariable Long id) {

        Order o = orderRepo.findDetailedById(id).orElseThrow();

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