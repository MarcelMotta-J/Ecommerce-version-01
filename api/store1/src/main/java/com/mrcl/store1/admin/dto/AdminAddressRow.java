package com.mrcl.store1.admin.dto;

public record AdminAddressRow(
        Long id,
        String street,
        String city,
        String state,
        String country,
        String zipCode
) {
}
