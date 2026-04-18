package com.mrcl.store1.auth.controller;

import com.mrcl.store1.auth.dto.*;
import com.mrcl.store1.auth.entity.AppUser;
import com.mrcl.store1.auth.entity.UserAddress;
import com.mrcl.store1.auth.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.getUserProfile(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication auth,
            @RequestBody UserProfileRequest request
    ) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.updateProfile(email, request));
    }

    // Address management
    @GetMapping("/addresses")
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.getUserAddresses(email));
    }

    @PostMapping("/addresses")
    public ResponseEntity<UserAddressResponse> addAddress(
            Authentication auth,
            @RequestBody UserAddressRequest request
    ) {
        String email = auth.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileService.addAddress(email, request));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<UserAddressResponse> updateAddress(
            Authentication auth,
            @PathVariable Long addressId,
            @RequestBody UserAddressRequest request
    ) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.updateAddress(email, addressId, request));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            Authentication auth,
            @PathVariable Long addressId
    ) {
        String email = auth.getName();
        userProfileService.deleteAddress(email, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/addresses/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(
            Authentication auth,
            @PathVariable Long addressId
    ) {
        String email = auth.getName();
        userProfileService.setDefaultAddress(email, addressId);
        return ResponseEntity.ok().build();
    }

    // Get orders for the authenticated user
    /*
    @GetMapping("/orders")
    public ResponseEntity<List<UserOrderResponse>> getUserOrders(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.getUserOrders(email));
    }

     */


    @GetMapping("/orders/{orderId}")
    public ResponseEntity<UserOrderDetailResponse> getOrderDetail(
            Authentication auth,
            @PathVariable Long orderId) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.getOrderDetail(email, orderId));
    }



}
