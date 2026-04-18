package com.mrcl.store1.auth.controller;

import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.auth.dto.UserOrderResponse;
import com.mrcl.store1.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

    private final OrderRepository orderRepository;

    public UserOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //
    @GetMapping
    public Page<UserOrderResponse> getUserOrders(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = auth.getName();

        Page<Order> ordersPage = orderRepository.findByAppUserEmailOrderByDateCreatedDesc(
                email,
                PageRequest.of(page, size)
        );

        return ordersPage.map(order -> new UserOrderResponse(
                order.getId(),
                order.getOrderTrackingNumber(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getTotalQuantity(),
                order.getDateCreated(),
                order.getOrderItems() != null ? order.getOrderItems().size() : 0
        ));
    }
}