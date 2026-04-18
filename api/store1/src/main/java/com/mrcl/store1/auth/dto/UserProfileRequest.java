package com.mrcl.store1.auth.dto;

//user buyer authentication

public record UserProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber
) {
}
