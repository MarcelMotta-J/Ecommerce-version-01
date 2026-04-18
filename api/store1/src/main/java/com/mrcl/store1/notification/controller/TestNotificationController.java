package com.mrcl.store1.notification.controller;

import com.mrcl.store1.notification.dto.TestNotificationRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/test-notification")
public class TestNotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    public TestNotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public Map<String, String> sendTestNotification(
            @RequestBody(required = false) TestNotificationRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        Map<String, Object> payload = Map.of(
                "id", System.currentTimeMillis(),
                "title", request != null && request.title() != null ? request.title() : "Teste de notificação",
                "message", request != null && request.message() != null ? request.message() : "Notificação enviada manualmente pelo backend.",
                "read", false,
                "createdAt", OffsetDateTime.now().toString(),
                "link", "/notifications"
        );

        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                payload
        );

        return Map.of("status", "ok");
    }

}
