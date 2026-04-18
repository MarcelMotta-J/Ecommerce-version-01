package com.mrcl.store1.notification.dto;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        boolean isRead,
        String createdAt,
        String link
) {}