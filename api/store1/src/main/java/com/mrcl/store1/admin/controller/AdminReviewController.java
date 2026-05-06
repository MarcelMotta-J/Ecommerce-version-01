package com.mrcl.store1.admin.controller;

import com.mrcl.store1.dao.ProductReviewRepository;
import com.mrcl.store1.dto.ProductReviewResponse;
import com.mrcl.store1.dto.ReviewDistributionResponse;
import com.mrcl.store1.dto.ReviewStatsResponse;
import com.mrcl.store1.entity.ProductReview;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final ProductReviewRepository reviewRepository;

    public AdminReviewController(ProductReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<ProductReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(r -> new ProductReviewResponse(
                        r.getId(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt(),
                        r.getProduct().getId(),
                        r.getProduct().getName(),
                        r.getUser().getId(),
                        r.getUser().getEmail()
                ))
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
    }

    @GetMapping("/stats")
    public ReviewStatsResponse getStats() {

        List<ProductReview> reviews = reviewRepository.findAll();

        double average = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);

        long total = reviews.size();

        return new ReviewStatsResponse(average, total);
    }

    @GetMapping("/distribution")
    public List<ReviewDistributionResponse> getDistribution() {
        return reviewRepository.getReviewDistribution();
    }

}