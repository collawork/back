package com.collawork.back.controller;

import com.collawork.back.model.Notification;
import com.collawork.back.model.User;
import com.collawork.back.repository.NotificationRepository;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestParam(value = "userId", required = false) Long userId) {
        System.out.println("알림에서 받은 아이디 : " + userId);

        if (userId == null) {
            System.out.println("사용자 아이디로 사용자 못참음 : " + userId);
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user);
        System.out.println("Notifications.size : " + notifications.size());
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/markAsRead/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return ResponseEntity.noContent().build();
    }
}
