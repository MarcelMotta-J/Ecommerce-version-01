package com.mrcl.store1.auth.dto;

public record UserAddressResponse(
        Long id,
        String street,
        String city,
        String state,
        String country,
        String zipCode,
        boolean isDefault
) {

}
