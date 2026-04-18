package com.mrcl.store1.admin.dto;

import java.time.Instant;
import java.util.Set;

public record AdminUserRow(
        Long id,
        String email,
        Set<String> roles,
        boolean enabled,
        Instant blockedAt
) {
}
