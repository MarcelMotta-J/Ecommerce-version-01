package com.mrcl.store1.admin.dto;

import java.time.Instant;

public record AdminActionLogRow(
        Long id,
        String adminEmail,
        String action,
        Instant timestamp
) {
}
