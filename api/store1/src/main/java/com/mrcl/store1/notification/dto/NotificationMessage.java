package com.mrcl.store1.notification.dto;

public record NotificationMessage(
        Long id,
        String title,
        String message,
        boolean isRead,
        String createdAt,
        String link
) {
}
