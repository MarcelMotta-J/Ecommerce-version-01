package com.mrcl.store1.dto;

public record ProductReviewRequest(
        Long productId,
        Integer rating,
        String comment
) {
}
