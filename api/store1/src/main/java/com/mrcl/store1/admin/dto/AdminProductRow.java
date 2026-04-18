package com.mrcl.store1.admin.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO returned in admin product listing.
 */
public record AdminProductRow(
        Long id,
        Long categoryId,
        String categoryName,
        String sku,
        String name,
        String description,
        BigDecimal unitPrice,
        String imageUrl,
        boolean active,
        int unitsInStock,
        Date dateCreated,
        Date lastUpdated
) {}

