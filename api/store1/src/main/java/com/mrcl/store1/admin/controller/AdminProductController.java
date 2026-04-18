package com.mrcl.store1.admin.controller;

import com.mrcl.store1.admin.dto.AdminPagedResponse;
import com.mrcl.store1.admin.dto.AdminProductRequest;
import com.mrcl.store1.admin.dto.AdminProductRow;
import com.mrcl.store1.admin.service.AdminActionLogService;
import com.mrcl.store1.dao.ProductCategoryRepository;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.entity.Product;
import com.mrcl.store1.entity.ProductCategory;
;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepo;
    private final ProductCategoryRepository categoryRepo;
    private final AdminActionLogService logService;

    public AdminProductController(ProductRepository productRepo,
                                  ProductCategoryRepository categoryRepo,
                                  AdminActionLogService logService) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.logService = logService;
    }

    /**
     * Admin product list with pagination and optional filters.
     *
     * Examples:
     * /api/admin/products?page=0&size=20
     * /api/admin/products?categoryId=1
     * /api/admin/products?active=true
     * /api/admin/products?name=java
     * /api/admin/products?categoryId=1&active=true&name=java&page=0&size=10
     */
    @GetMapping
    public AdminPagedResponse<AdminProductRow> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> result;

        boolean hasCategory = categoryId != null;
        boolean hasActive = active != null;
        boolean hasName = name != null && !name.isBlank();

        if (hasCategory && hasActive && hasName) {
            result = productRepo.findByCategoryIdAndNameContainingAndActive(categoryId, name, active, pageable);
        } else if (hasCategory && hasActive) {
            result = productRepo.findByCategoryIdAndActive(categoryId, active, pageable);
        } else if (hasCategory && hasName) {
            result = productRepo.findByCategoryIdAndNameContaining(categoryId, name, pageable);
        } else if (hasActive && hasName) {
            result = productRepo.findByNameContainingAndActive(name, active, pageable);
        } else if (hasCategory) {
            result = productRepo.findByCategoryId(categoryId, pageable);
        } else if (hasActive) {
            result = productRepo.findByActive(active, pageable);
        } else if (hasName) {
            result = productRepo.findByNameContaining(name, pageable);
        } else {
            result = productRepo.findAll(pageable);
        }

        return new AdminPagedResponse<>(
                result.getContent().stream().map(this::toRow).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    
    @GetMapping("/{id}")
    public AdminProductRow getProduct(@PathVariable Long id) {
        Product p = productRepo.findById(id).orElseThrow();
        return toRow(p);
    }

    @PostMapping
    public AdminProductRow createProduct(@RequestBody AdminProductRequest req) {
        ProductCategory category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + req.categoryId()));

        Product p = new Product();
        p.setCategory(category);
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setUnitPrice(req.unitPrice());
        p.setImageUrl(req.imageUrl());
        p.setActive(req.active());
        p.setUnitsInStock(req.unitsInStock());

        Product saved = productRepo.save(p);
        return toRow(saved);
    }

    @PutMapping("/{id}")
    public AdminProductRow updateProduct(@PathVariable Long id,
                                         @RequestBody AdminProductRequest req) {
        Product p = productRepo.findById(id).orElseThrow();

        ProductCategory category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + req.categoryId()));

        p.setCategory(category);
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setUnitPrice(req.unitPrice());
        p.setImageUrl(req.imageUrl());
        p.setActive(req.active());
        p.setUnitsInStock(req.unitsInStock());

        Product saved = productRepo.save(p);
        return toRow(saved);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        Product p = productRepo.findById(id).orElseThrow();
        productRepo.delete(p);
    }

    @PatchMapping("/{id}/stock")
    public void updateStock(
            @PathVariable Long id,
            @RequestParam int unitsInStock,
            Authentication auth
    ) {
        Product p = productRepo.findById(id).orElseThrow();
        p.setUnitsInStock(unitsInStock);
        productRepo.save(p);
        logService.log(auth, "Updated stock for product #" + id + " to " + unitsInStock);
    }

    @PatchMapping("/{id}/active")
    public void updateActive(
            @PathVariable Long id,
            @RequestParam boolean active,
            Authentication auth
    ) {

        Product p = productRepo.findById(id).orElseThrow();
        p.setActive(active);
        productRepo.save(p);
        logService.log(auth, (active ? "Enabled" : "Disabled") + " product #" + id);
    }

    private AdminProductRow toRow(Product p) {
        Long categoryId = (p.getCategory() != null) ? p.getCategory().getId() : null;
        String categoryName = (p.getCategory() != null) ? p.getCategory().getCategoryName() : null;

        return new AdminProductRow(
                p.getId(),
                categoryId,
                categoryName,
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getUnitPrice(),
                p.getImageUrl(),
                p.isActive(),
                p.getUnitsInStock(),
                p.getDateCreated(),
                p.getLastUpdated()
        );
    }
}