package com.mrcl.store1.auth.dto;

public record UserAddressRequest(
        String street,
        String city,
        String state,
        String country,
        String zipCode,
        boolean isDefault
) {
}
