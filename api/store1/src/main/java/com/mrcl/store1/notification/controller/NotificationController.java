package com.mrcl.store1.notification.controller;

import com.mrcl.store1.notification.dao.NotificationRepository;
import com.mrcl.store1.notification.dto.NotificationResponse;
import com.mrcl.store1.notification.entity.Notification;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository repository) {
        this.notificationRepository = repository;
    }

    // Endpoint do dropdown
    @GetMapping("/latest")
    public List<NotificationResponse> getLatestNotifications(Authentication auth) {
        String email = auth.getName();

        return notificationRepository.findTop10ByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    //Endpoint da página completa
    @GetMapping
    public List<NotificationResponse> getAllNotifications(Authentication auth) {
        String email = auth.getName();

        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt().toString(),
                notification.getLink()
        );
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getUserEmail().equals(email)) {
            throw new IllegalArgumentException("Notification does not belong to this user");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead(Authentication auth) {
        String email = auth.getName();

        List<Notification> notifications = notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }


}
