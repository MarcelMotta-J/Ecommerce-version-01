package com.mrcl.store1.service;

import com.mrcl.store1.auth.dao.AppUserRepository;
import com.mrcl.store1.auth.entity.AppUser;
import com.mrcl.store1.auth.entity.UserAddress;
import com.mrcl.store1.dao.CustomerRepository;
import com.mrcl.store1.dto.Purchase;
import com.mrcl.store1.dto.PurchaseResponse;
import com.mrcl.store1.entity.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mrcl.store1.auth.dao.UserAddressRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;
    private final UserAddressRepository userAddressRepository;

    public CheckoutServiceImpl(
            CustomerRepository customerRepository,
            AppUserRepository appUserRepository,
            UserAddressRepository userAddressRepository
    ) {
        this.customerRepository = customerRepository;
        this.appUserRepository = appUserRepository;
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase, String userEmail) {

        // 1) Order info from DTO
        Order order = purchase.getOrder();

        // 2) Generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        order.setStatus(OrderStatus.PENDING);

        // 3) Attach logged-in account
        //System.out.println("Logged user email: " + userEmail);
        log.info("Logged user email: {}", userEmail);
        if (userEmail != null) {
            appUserRepository.findByEmail(userEmail).ifPresent(user -> {
                //System.out.println("User found: " + user.getEmail());
                log.info("User found: {}", user.getEmail());
                order.setAppUser(user);
            });
        } else {
            //System.out.println("No authenticated user found");
            log.warn("No authenticated user found");
        }

        // 4) Add items
        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(order::add);

        // 5) Addresses
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        // 6) Customer
        Customer customer = purchase.getCustomer();
        customer.add(order);

        // 7) Save order via customer cascade
        customerRepository.save(customer);

        // 8) Save shipping address into user profile without duplicating
        if (userEmail != null && purchase.getShippingAddress() != null) {
            findOrCreateUserAddress(userEmail, purchase.getShippingAddress());
        }

        /*
        // salvar billing address
        if (!purchase.getBillingAddress().equals(purchase.getShippingAddress())) {
            findOrCreateUserAddress(userEmail, purchase.getBillingAddress());
        }
         */

        // evitar salvar billing address idêntico ao shipping
        if (userEmail != null && purchase.getShippingAddress() != null) {
            findOrCreateUserAddress(userEmail, purchase.getShippingAddress());
        }

        if (userEmail != null && purchase.getBillingAddress() != null) {
            Address billing = purchase.getBillingAddress();
            Address shipping = purchase.getShippingAddress();

            boolean sameAsShipping =
                    shipping != null &&
                            normalize(billing.getStreet()).equals(normalize(shipping.getStreet())) &&
                            normalize(billing.getCity()).equals(normalize(shipping.getCity())) &&
                            normalize(billing.getState()).equals(normalize(shipping.getState())) &&
                            normalize(billing.getCountry()).equals(normalize(shipping.getCountry())) &&
                            normalizeZip(billing.getZipCode()).equals(normalizeZip(shipping.getZipCode()));

            if (!sameAsShipping) {
                findOrCreateUserAddress(userEmail, billing);
            }
        }



        return new PurchaseResponse(orderTrackingNumber);
    }



    private UserAddress findOrCreateUserAddress(String email, Address shippingAddress) {

        Optional<UserAddress> existingAddress =
                userAddressRepository.findByUserEmailAndStreetAndCityAndStateAndCountryAndZipCode(
                        email,
                        normalize(shippingAddress.getStreet()),
                        normalize(shippingAddress.getCity()),
                        normalize(shippingAddress.getState()),
                        normalize(shippingAddress.getCountry()),
                        normalize(shippingAddress.getZipCode())
                );

        if (existingAddress.isPresent()) {
            return existingAddress.get();
        }

        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        UserAddress newAddress = new UserAddress();
        newAddress.setUser(appUser);
        newAddress.setStreet(normalize(shippingAddress.getStreet()));
        newAddress.setCity(normalize(shippingAddress.getCity()));
        newAddress.setState(normalize(shippingAddress.getState()));
        newAddress.setCountry(normalize(shippingAddress.getCountry()));
        newAddress.setZipCode(normalizeZip(shippingAddress.getZipCode()));

        // Se ainda não houver default, este vira default
        //boolean hasDefault = userAddressRepository.existsByUserEmailAndIsDefaultTrue(email);
        //newAddress.setDefault(!hasDefault);
        // ✔ sempre o último usado vira default
        userAddressRepository.resetDefaultForUser(email);
        newAddress.setDefault(true);

        return userAddressRepository.save(newAddress);
    }


    private String generateOrderTrackingNumber() {
        return UUID.randomUUID().toString();
    }

    private String normalize(String value) {
        if (value == null) return null;
        return value.trim().toLowerCase();
    }

    private String normalizeZip(String value) {
        if (value == null) return null;
        return value.trim();
    }


}
