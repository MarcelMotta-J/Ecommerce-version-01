package com.mrcl.store1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "status", "UP",
                "service", "Ecommerce API",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now(),
                "endpoints", Map.of(
                        "products", "/api/products",
                        "auth", "/api/auth",
                        "categories", "/api/product-category"
                )
        );
    }
}