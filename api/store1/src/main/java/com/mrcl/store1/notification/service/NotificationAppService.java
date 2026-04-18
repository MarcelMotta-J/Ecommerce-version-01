package com.mrcl.store1.notification.service;

import com.mrcl.store1.notification.dao.NotificationRepository;
import com.mrcl.store1.notification.dto.NotificationMessage;
import com.mrcl.store1.notification.entity.Notification;
import org.springframework.stereotype.Service;

import java.time.Instant;

    @Service
    public class NotificationAppService {

        private final NotificationRepository notificationRepository;
        private final NotificationPushService notificationPushService;

        public NotificationAppService(NotificationRepository notificationRepository,
                                      NotificationPushService notificationPushService) {
            this.notificationRepository = notificationRepository;
            this.notificationPushService = notificationPushService;
        }

        public void createAndSend(String userEmail, String title, String message, String link) {
            Notification notification = new Notification();
            notification.setUserEmail(userEmail);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRead(false);
            notification.setCreatedAt(Instant.now());
            notification.setLink(link);

            Notification saved = notificationRepository.save(notification);

            NotificationMessage notificationMessage = new NotificationMessage(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getMessage(),
                    saved.isRead(),
                    saved.getCreatedAt().toString(),
                    saved.getLink()
            );

            notificationPushService.sendToUser(userEmail, notificationMessage);
        }
}
