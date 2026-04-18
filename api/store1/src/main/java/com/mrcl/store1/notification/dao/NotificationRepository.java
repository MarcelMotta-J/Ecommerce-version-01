package com.mrcl.store1.notification.dao;

import com.mrcl.store1.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // list all messages
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // list 10 messages in dropdown notification
    List<Notification> findTop10ByUserEmailOrderByCreatedAtDesc(String email);

    Optional<Notification> findByIdAndUserEmail(Long id, String email);

}