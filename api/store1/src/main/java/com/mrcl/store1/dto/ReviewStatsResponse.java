package com.mrcl.store1.dto;

public record ReviewStatsResponse(
        Double averageRating,
        Long totalReviews
) {
}
