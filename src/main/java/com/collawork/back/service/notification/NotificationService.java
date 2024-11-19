package com.collawork.back.service.notification;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.notification.Notification;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createNotification(Long userId, String type, String message, Long requestId, Long projectId) {
        System.out.println("알림 생성 요청 - 사용자 ID: " + userId + ", 유형: " + type);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.Type.valueOf(type));
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setIsActionCompleted(false);

        // 요청 ID 설정 (친구 요청 알림)
        if ("FRIEND_REQUEST".equals(type) && requestId != null) {
            notification.setRequestId(requestId);
        }

        // 프로젝트 ID 설정 (프로젝트 초대 알림)
        if ("PROJECT_INVITATION".equals(type) && projectId != null) {
            notification.setRequestId(projectId);
        } else if ("PROJECT_INVITATION".equals(type)) {
            throw new IllegalArgumentException("프로젝트 초대에는 프로젝트 ID가 필요합니다.");
        }

        // 알림 저장
        notificationRepository.save(notification);

        System.out.println("알림 저장 완료: " + notification.getId());
    }



}

