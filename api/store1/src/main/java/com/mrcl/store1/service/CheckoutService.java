package com.mrcl.store1.service;

import com.mrcl.store1.auth.entity.UserAddress;
import com.mrcl.store1.dto.Purchase;
import com.mrcl.store1.dto.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase, String userEmail);




}
