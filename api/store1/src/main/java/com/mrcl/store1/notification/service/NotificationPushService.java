package com.mrcl.store1.notification.service;

import com.mrcl.store1.notification.dto.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPushService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPushService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(String userEmail, NotificationMessage notification) {
        System.out.println("Sending websocket notification to user: " + userEmail);
        System.out.println("Notification title: " + notification.title());

        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/notifications",
                notification
        );
    }

}
