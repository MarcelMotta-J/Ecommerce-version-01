package com.mrcl.store1.controller;

import com.mrcl.store1.auth.dao.AppUserRepository;
import com.mrcl.store1.auth.entity.AppUser;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.dao.ProductReviewRepository;
import com.mrcl.store1.dto.ProductReviewRequest;
import com.mrcl.store1.dto.ProductReviewResponse;
import com.mrcl.store1.entity.Product;
import com.mrcl.store1.entity.ProductReview;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin // ajuda no frontend local
public class ProductReviewController {

    private final ProductReviewRepository reviewRepository;
    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductReviewController(ProductReviewRepository reviewRepository, AppUserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 🔎 buscar avaliações por produto
    @GetMapping("/product/{productId}")
    public List<ProductReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(review -> new ProductReviewResponse(
                        review.getId(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt(),
                        review.getProduct().getId(),
                        review.getProduct().getName(),
                        review.getUser().getId(),
                        review.getUser().getEmail()
                ))
                .toList();
    }

    // ⭐ criar avaliação
    @PostMapping
    public ProductReviewResponse createReview(
            @RequestBody ProductReviewRequest request,
            Authentication authentication
    ) {
        AppUser user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<ProductReview> existing = reviewRepository
                .findByProductIdAndUserId(product.getId(), user.getId());

        ProductReview review;

        if (existing.isPresent()) {
            review = existing.get();
        } else {
            review = new ProductReview();
            review.setProduct(product);
            review.setUser(user);
            review.setCreatedAt(LocalDateTime.now());
        }

        review.setRating(request.rating());
        review.setComment(request.comment());

        ProductReview saved = reviewRepository.save(review);

        return new ProductReviewResponse(
                saved.getId(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt(),
                saved.getProduct().getId(),
                saved.getProduct().getName(),
                saved.getUser().getId(),
                saved.getUser().getEmail()
        );
    }


}