package com.mrcl.store1.controller;

import com.mrcl.store1.dao.FavoriteRepository;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.entity.Favorite;
import com.mrcl.store1.entity.Product;
import com.mrcl.store1.notification.service.NotificationAppService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final NotificationAppService notificationAppService;

    public FavoriteController(FavoriteRepository favoriteRepository, ProductRepository productRepository, NotificationAppService notificationAppService) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.notificationAppService = notificationAppService;
    }

    @GetMapping
    public List<Product> getFavorites(Authentication auth) {
        String email = auth.getName();

        List<Long> productIds = favoriteRepository.findByUserEmail(email)
                .stream()
                .map(Favorite::getProductId)
                .toList();

        if (productIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findAllById(productIds);
    }

    @PostMapping("/{productId}")
    public void addFavorite(@PathVariable Long productId, Authentication auth) {
        String email = auth.getName();

        // Only save and notify if favorite does not already exist
        if (!favoriteRepository.existsByUserEmailAndProductId(email, productId)) {
            Favorite favorite = new Favorite();
            favorite.setUserEmail(email);
            favorite.setProductId(productId);
            favoriteRepository.save(favorite);

            notificationAppService.createAndSend(
                    email,
                    "Favorite added",
                    "A product was added to your favorites successfully.",
                    "/favorites"
            );
        }
    }

    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable Long productId, Authentication auth) {
        String email = auth.getName();
        long deleted = favoriteRepository.deleteByUserEmailAndProductId(email, productId);
        System.out.println("Deleted favorites: " + deleted);
    }
}
