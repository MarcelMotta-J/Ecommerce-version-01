package com.mrcl.store1.auth.dto;


/**
 * DTO used when user registers.
 * Contains raw password from client.
 */
public record RegisterRequest(
        String email,
        String password

) {


}
