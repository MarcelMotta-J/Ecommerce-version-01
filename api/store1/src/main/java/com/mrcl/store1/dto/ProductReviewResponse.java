package com.mrcl.store1.dto;
import java.time.LocalDateTime;
public record ProductReviewResponse(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        Long productId,
        String productName,
        Long userId,
        String userEmail
) {
}
