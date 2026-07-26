package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminOrderDetailsResponse;
import com.mrcl.store1.admin.dto.AdminOrderItemRow;
import com.mrcl.store1.admin.dto.AdminOrderRow;
import com.mrcl.store1.admin.dto.AdminCustomerRow;
import com.mrcl.store1.admin.dto.AdminAddressRow;
import com.mrcl.store1.admin.dto.UpdateOrderStatusRequest;
import com.mrcl.store1.admin.dto.AdminPagedResponse;
import com.mrcl.store1.admin.service.AdminActionLogService;
import com.mrcl.store1.admin.service.AdminDashboardService;
import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;



@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepo;

    private final AdminActionLogService logService;

    private final AdminDashboardService adminDashboardService;



    public AdminOrderController(OrderRepository orderRepo, AdminActionLogService logService, AdminDashboardService adminDashboardService) {
        this.orderRepo = orderRepo;
        this.logService = logService;
        this.adminDashboardService = adminDashboardService;
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

        return adminDashboardService.listOrders(
                page,
                size,
                status,
                customerEmail
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
        return adminDashboardService.getOrderDetails(id);

    }



}