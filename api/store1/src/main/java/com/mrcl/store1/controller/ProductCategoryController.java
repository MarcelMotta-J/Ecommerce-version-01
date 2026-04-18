package com.mrcl.store1.controller;

import com.mrcl.store1.entity.ProductCategory;
import com.mrcl.store1.dao.ProductCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryController(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        List<ProductCategory> categories = productCategoryRepository.findAll();

        int totalElements = categories.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<ProductCategory> pagedCategories;

        if (fromIndex >= totalElements) {
            pagedCategories = List.of();
        } else {
            pagedCategories = categories.subList(fromIndex, toIndex);
        }

        Map<String, Object> embedded = new HashMap<>();
        embedded.put("productCategory", pagedCategories);

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("size", size);
        pageInfo.put("totalElements", totalElements);
        pageInfo.put("totalPages", (int) Math.ceil((double) totalElements / size));
        pageInfo.put("number", page);

        Map<String, Object> response = new HashMap<>();
        response.put("_embedded", embedded);
        response.put("page", pageInfo);

        return ResponseEntity.ok(response);
    }
}