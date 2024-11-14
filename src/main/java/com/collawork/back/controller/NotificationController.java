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
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam("userId") Long userId) {
        System.out.println("알림 조회 요청 userId: " + userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + userId);
            return ResponseEntity.notFound().build();
        }

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user);
        System.out.println("가져온 미확인 알림 수: " + notifications.size());

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
