package com.mrcl.store1.controller;

import com.mrcl.store1.auth.dto.UserAddressResponse;
import com.mrcl.store1.auth.entity.UserAddress;
import com.mrcl.store1.auth.service.UserProfileService;
import com.mrcl.store1.dto.Purchase;
import com.mrcl.store1.dto.PurchaseResponse;
import com.mrcl.store1.notification.service.NotificationAppService;
import com.mrcl.store1.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {


    private final CheckoutService checkoutService;
    private final NotificationAppService notificationAppService;
    private final UserProfileService userProfileService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, NotificationAppService notificationAppService, UserProfileService userProfileService) {
        this.checkoutService = checkoutService;
        this.notificationAppService = notificationAppService;
        this.userProfileService = userProfileService;
    }

    // http://localhost:8080/api/checkout/purchase
    @PostMapping("/purchase")
    public PurchaseResponse placeOrder(@RequestBody Purchase purchase,
                                       Authentication authentication
    ) {
        String userEmail = authentication.getName();

        PurchaseResponse response = checkoutService.placeOrder(purchase, userEmail);

        notificationAppService.createAndSend(
                purchase.getCustomer().getEmail(),
                "Order confirmed",
                "Your order has been placed successfully.",
                "/orders"
        );

        return response;
    }

    // http://localhost:8080/api/addresses?page=0&size=10&sort=id,desc
    @GetMapping("/addresses/paged")
    public ResponseEntity<Page<UserAddressResponse>> getUserAddressesPaged(
            Authentication auth,
            Pageable pageable
    ) {
        String email = auth.getName();
        return ResponseEntity.ok(userProfileService.getUserAddressesPaged(email, pageable));
    }

    @GetMapping("/addresses/search")
    public Page<UserAddressResponse> searchAddresses(
            Authentication auth,
            @RequestParam String city,
            Pageable pageable
    ) {
        return userProfileService.getAddressesByCity(auth.getName(), city, pageable);
    }

    // http://localhost:8080/api/addresses/ordered?page=0&size=10
    @GetMapping("/addresses/ordered")
    public ResponseEntity<Page<UserAddressResponse>> getOrderedAddresses(
            Authentication auth,
            Pageable pageable
    ) {
        String email = auth.getName();
        return ResponseEntity.ok(
                userProfileService.getUserAddressesOrdered(email, pageable)
        );
    }
}
