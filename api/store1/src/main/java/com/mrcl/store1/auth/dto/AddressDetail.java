package com.mrcl.store1.auth.dto;

public record AddressDetail(
        String street,
        String city,
        String state,
        String country,
        String zipCode
) {
}
