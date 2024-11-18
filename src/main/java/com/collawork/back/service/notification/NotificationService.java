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
    public void createNotification(Long userId, String type, String message, Long requestId) {
        System.out.println("알림 생성 요청 - 사용자 ID: " + userId + ", 유형: " + type);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.Type.valueOf(type));
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setIsActionCompleted(false);

        // PROJECT_INVITATION 유형일 경우 requestId가 필수로 설정해야됨(근데 아니어도 되는거 생각 해봐야됨)
        if ("PROJECT_INVITATION".equals(type)) {
            if (requestId == null) {
                throw new IllegalArgumentException("프로젝트 초대에는 요청 ID가 필요합니다.");
            }
            notification.setRequestId(requestId);  // 프로젝트 초대 알림에만 requestId 설정
        }

        // 알림 저장
        notificationRepository.save(notification);

        System.out.println("알림 저장 완료: " + notification.getId());
    }


}

