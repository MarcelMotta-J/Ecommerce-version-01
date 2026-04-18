package com.mrcl.store1.dao;

import com.mrcl.store1.admin.dto.AdminProductStockView;
import com.mrcl.store1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(@Param("id") Long id, Pageable pageable);

    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);

    // Additional methods for admin
    Page<Product> findByActive(boolean active, Pageable pageable);

    Page<Product> findByCategoryIdAndActive(Long categoryId, boolean active, Pageable pageable);

    Page<Product> findByNameContainingAndActive(String name, boolean active, Pageable pageable);

    Page<Product> findByCategoryIdAndNameContaining(Long categoryId, String name, Pageable pageable);

    Page<Product> findByCategoryIdAndNameContainingAndActive(Long categoryId, String name, boolean active, Pageable pageable);

    /**
     * Find top products by stock quantity
     * Returns products with highest stock first, limited to 10
     */
    @Query(value = """
        SELECT p.name AS name, p.units_in_stock AS unitsInStock
        FROM product p
        WHERE p.active = true
        ORDER BY p.units_in_stock DESC
        LIMIT 10
        """, nativeQuery = true)
    List<AdminProductStockView> findTopProductsByStock();
}