package com.mrcl.store1.admin.dto;

public record AdminCustomerEditResponse(
        Long id,
        String firstName,
        String lastName,
        String email

) {
}
