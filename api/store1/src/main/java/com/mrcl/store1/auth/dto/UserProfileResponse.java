package com.mrcl.store1.auth.dto;

import java.time.Instant;
import java.util.Set;

//user buyer authentication

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        boolean enabled,
        Instant createdAt,
        Set<String> roles
) {
}
