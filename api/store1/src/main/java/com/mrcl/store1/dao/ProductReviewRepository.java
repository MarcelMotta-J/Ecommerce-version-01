package com.mrcl.store1.dao;

import com.mrcl.store1.dto.ReviewDistributionResponse;
import com.mrcl.store1.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductId(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Optional<ProductReview> findByProductIdAndUserId(Long productId, Long userId);

    List<ProductReview> findAllByOrderByCreatedAtDesc();


    @Query("""
    SELECT new com.mrcl.store1.dto.ReviewDistributionResponse(
        r.rating,
        COUNT(r)
    )
    FROM ProductReview r
    GROUP BY r.rating
    ORDER BY r.rating
""")
    List<ReviewDistributionResponse> getReviewDistribution();
}
