package com.mrcl.store1.dao;

import com.mrcl.store1.admin.dto.AdminRevenuePerDayView;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import com.mrcl.store1.admin.dto.AdminOrdersPerDayView;
import com.mrcl.store1.admin.dto.AdminTopProductView;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "appUser",
            "customer",
            "shippingAddress",
            "billingAddress",
            "orderItems"
    })
    Optional<Order> findDetailedById(Long id);

    Page<Order> findAllByOrderByDateCreatedDesc(Pageable pageable);

    Page<Order> findByStatusOrderByDateCreatedDesc(OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(String customerEmail, Pageable pageable);

    Page<Order> findByStatusAndCustomerEmailContainingIgnoreCaseOrderByDateCreatedDesc(
            OrderStatus status,
            String customerEmail,
            Pageable pageable
    );

    List<Order> findAllByOrderByDateCreatedDesc();

    List<Order> findByStatusOrderByDateCreatedDesc(OrderStatus status);

    long countByStatus(OrderStatus status);

    // orders per day chart
    @Query(value = """
            SELECT DATE(o.date_created) AS day, COUNT(o.id) AS ordersCount
            FROM orders o
            GROUP BY DATE(o.date_created)
            ORDER BY DATE(o.date_created)
            """, nativeQuery = true)
    List<AdminOrdersPerDayView> findOrdersPerDay();

    // revenue per day chart
    @Query(value = """
            SELECT DATE(o.date_created) AS day, COALESCE(SUM(o.total_price), 0) AS revenue
            FROM orders o
            GROUP BY DATE(o.date_created)
            ORDER BY DATE(o.date_created)
            """, nativeQuery = true)
    List<AdminRevenuePerDayView> findRevenuePerDay();

    // top products chart
    @Query(value = """
            SELECT p.name AS name, SUM(oi.quantity) AS totalSold
            FROM order_item oi
            JOIN product p ON p.id = oi.product_id
            GROUP BY p.name
            ORDER BY totalSold DESC
            LIMIT 5
            """, nativeQuery = true)
    List<AdminTopProductView> findTopProducts();

    // ===== ADD THESE TWO NEW METHODS FOR USER ORDERS =====

    List<Order> findByAppUserEmailOrderByDateCreatedDesc(String email);

    List<Order> findByAppUserIdOrderByDateCreatedDesc(Long userId);



    @EntityGraph(attributePaths = {"orderItems"})
    Page<Order> findByAppUserEmailOrderByDateCreatedDesc(String email, Pageable pageable);
}