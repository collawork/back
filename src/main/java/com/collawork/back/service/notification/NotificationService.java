package com.collawork.back.service.notification;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.notification.Notification;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNotification(Long userId, String type, String message, Long projectId) {
        // User 엔터티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user); // User 객체 설정
        notification.setType(Notification.Type.valueOf(type)); // Enum 타입으로 변환
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setRequestId(projectId); // 프로젝트 ID를 알림에 저장
        notification.setIsActionCompleted(false);

        notificationRepository.save(notification);
    }

}

