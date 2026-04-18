package com.mrcl.store1.auth.service;

import com.mrcl.store1.auth.dao.AppUserRepository;
import com.mrcl.store1.auth.dao.UserAddressRepository;
import com.mrcl.store1.auth.dto.*;
import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.dao.ProductRepository;
import com.mrcl.store1.auth.entity.AppUser;
import com.mrcl.store1.auth.entity.UserAddress;
import com.mrcl.store1.entity.Address;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.Product;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final AppUserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAddressRepository userAddressRepository;

    public UserProfileService(
            AppUserRepository userRepository,
            UserAddressRepository addressRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder, UserAddressRepository userAddressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAddressRepository = userAddressRepository;
    }

    //Ativar cache
    @Cacheable(value = "userAddresses", key = "#email")
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToProfileResponse(user);
    }


    // Invalidar cache
    @CacheEvict(value = "userAddresses", key = "#email")
    @Transactional
    public UserProfileResponse updateProfile(String email, UserProfileRequest request) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());

        user = userRepository.save(user);
        return mapToProfileResponse(user);
    }

    //Ativar cache
    @Cacheable(value = "userAddresses", key = "#email")
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getUserAddresses(String email) {
        return addressRepository.findByUserEmailOrderByIsDefaultDesc(email)
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }


    // Invalidar cache
    @CacheEvict(
            value = {
                    "userAddresses",
                    "userAddressesPaged",
                    "userAddressesOrdered",
                    "userAddressesByCity"
            },
            allEntries = true
    )
    @Transactional
    public UserAddressResponse addAddress(String email, UserAddressRequest request) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setZipCode(request.zipCode());

        if (request.isDefault()) {
            addressRepository.resetDefaultForUser(email);
            address.setDefault(true);
        } else {
            boolean hasDefault = addressRepository.existsByUserEmailAndIsDefaultTrue(email);
            address.setDefault(!hasDefault);
        }

        address = addressRepository.save(address);
        return mapToAddressResponse(address);
    }

    // Invalidar cache
    @CacheEvict(
            value = {
                    "userAddresses",
                    "userAddressesPaged",
                    "userAddressesOrdered",
                    "userAddressesByCity"
            },
            allEntries = true
    )
    @Transactional
    public UserAddressResponse updateAddress(String email, Long addressId, UserAddressRequest request) {
        UserAddress address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setZipCode(request.zipCode());

        if (request.isDefault() && !address.isDefault()) {
            addressRepository.resetDefaultForUser(email);
            address.setDefault(true);
        }

        address = addressRepository.save(address);
        return mapToAddressResponse(address);
    }

    // Invalidar cache
    @CacheEvict(
            value = {
                    "userAddresses",
                    "userAddressesPaged",
                    "userAddressesOrdered",
                    "userAddressesByCity"
            },
            allEntries = true
    )
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        UserAddress address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);

        if (address.isDefault()) {
            addressRepository.findByUserEmailOrderByIsDefaultDesc(email)
                    .stream()
                    .findFirst()
                    .ifPresent(a -> {
                        a.setDefault(true);
                        addressRepository.save(a);
                    });
        }
    }

    // Invalidar cache
    @CacheEvict(
            value = {
                    "userAddresses",
                    "userAddressesPaged",
                    "userAddressesOrdered",
                    "userAddressesByCity"
            },
            allEntries = true
    )
    @Transactional
    public void setDefaultAddress(String email, Long addressId) {
        addressRepository.resetDefaultForUser(email);

        UserAddress address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setDefault(true);
        addressRepository.save(address);
    }


    @Transactional(readOnly = true)
    public List<UserOrderResponse> getUserOrders(String email) {
        List<Order> orders = orderRepository.findByAppUserEmailOrderByDateCreatedDesc(email);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserOrderDetailResponse getOrderDetail(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify order belongs to user
        if (order.getAppUser() == null || !order.getAppUser().getEmail().equals(email)) {
            throw new RuntimeException("Order not found for this user");
        }

        // Create OrderItemDetail list
        List<OrderItemDetail> items = order.getOrderItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);
                    return new OrderItemDetail(
                            item.getProductId(),
                            product != null ? product.getName() : "Product not found",
                            item.getImageUrl(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
                })
                .collect(Collectors.toList());

        return new UserOrderDetailResponse(
                order.getId(),
                order.getOrderTrackingNumber(),
                order.getStatus().toString(),
                order.getTotalPrice(),
                order.getTotalQuantity(),
                order.getDateCreated(),
                order.getDateUpdated(),
                items,
                mapAddressToDetail(order.getShippingAddress()),
                mapAddressToDetail(order.getBillingAddress())
        );
    }

    private UserProfileResponse mapToProfileResponse(AppUser user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getRoles()
        );
    }

    private UserAddressResponse mapToAddressResponse(UserAddress address) {
        return new UserAddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode(),
                address.isDefault()
        );
    }

    private UserOrderResponse mapToOrderResponse(Order order) {
        return new UserOrderResponse(
                order.getId(),
                order.getOrderTrackingNumber(),
                order.getStatus().toString(),
                order.getTotalPrice(),
                order.getTotalQuantity(),
                order.getDateCreated(),
                order.getOrderItems() != null ? order.getOrderItems().size() : 0
        );
    }

    private AddressDetail mapAddressToDetail(Address address) {
        if (address == null) return null;
        return new AddressDetail(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode()
        );
    }

    private UserAddressResponse mapToUserAddressResponse(UserAddress address) {
        return new UserAddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode(),
                address.isDefault()
        );
    }

    @Cacheable(value = "userAddressesPaged", key = "#email + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<UserAddressResponse> getUserAddressesPaged(String email, Pageable pageable) {
        return userAddressRepository
                .findByUserEmailOrderByIsDefaultDesc(email, pageable)
                .map(this::mapToUserAddressResponse);
    }

    @Cacheable(value = "userAddressesByCity", key = "#email + '_' + #city + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<UserAddressResponse> getAddressesByCity(
            String email, String city, Pageable pageable) {

        return userAddressRepository
                .findByUserEmailAndCityContainingIgnoreCase(email, city, pageable)
                .map(this::mapToUserAddressResponse);
    }

    @Cacheable(value = "userAddressesOrdered", key = "#email + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<UserAddressResponse> getUserAddressesOrdered(
            String email,
            Pageable pageable
    ) {
        return userAddressRepository
                .findByUserEmailOrderByIsDefaultDescCreatedAtDesc(email, pageable)
                .map(this::mapToUserAddressResponse);
    }

}