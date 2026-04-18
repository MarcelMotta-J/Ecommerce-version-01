package com.mrcl.store1.admin.dto;

import java.math.BigDecimal;

/**
 * DTO used by admin to create/update products.
 * We send categoryId instead of full ProductCategory object.
 */

public record AdminProductRequest(

        Long categoryId,
        String sku,
        String name,
        String description,
        BigDecimal unitPrice,
        String imageUrl,
        boolean active,
        int unitsInStock
) {
}
