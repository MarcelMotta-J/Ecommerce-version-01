package com.mrcl.store1.controller;

import com.mrcl.store1.entity.Product;
import com.mrcl.store1.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Product> productPage = productService.getAllProducts(page, size);

            Map<String, Object> response = buildPagedResponse(productPage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping("/search/findByCategoryId")
    public ResponseEntity<Map<String, Object>> findByCategoryId(
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Product> productPage = productService.findByCategoryId(id, page, size);

            Map<String, Object> response = buildPagedResponse(productPage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/findByNameContaining")
    public ResponseEntity<Map<String, Object>> findByNameContaining(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Product> productPage = productService.findByNameContaining(name, page, size);

            Map<String, Object> response = buildPagedResponse(productPage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Map<String, Object> buildPagedResponse(Page<Product> productPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("_embedded", Map.of("products", productPage.getContent()));
        response.put("page", Map.of(
                "size", productPage.getSize(),
                "totalElements", productPage.getTotalElements(),
                "totalPages", productPage.getTotalPages(),
                "number", productPage.getNumber()
        ));
        return response;
    }
}