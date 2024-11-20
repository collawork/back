package com.collawork.back.service.notification;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.notification.Notification;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 알림 생성 메서드
     * @param userId 알림을 받을 사용자 ID
     * @param type 알림 유형 (FRIEND_REQUEST, PROJECT_INVITATION 등)
     * @param message 알림 메시지
     * @param requestId 요청 ID (친구 요청 ID 또는 프로젝트 ID)
     * @param projectId 프로젝트 ID (프로젝트 초대 알림일 경우)
     */
    @Transactional
    public void createNotification(Long userId, String type, String message, Long friendRequestId, Long projectId) {
        System.out.println("알림 생성 요청 - 사용자 ID: " + userId + ", 유형: " + type);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.Type.valueOf(type));
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setIsActionCompleted(false);

        // FRIEND_REQUEST 알림
        if ("FRIEND_REQUEST".equals(type)) {
            if (friendRequestId != null) {
                notification.setRequestId(friendRequestId);
            } else {
                throw new IllegalArgumentException("친구 요청 알림에는 friendRequestId가 필요합니다.");
            }
        }

        // PROJECT_INVITATION 알림
        if ("PROJECT_INVITATION".equals(type)) {
            if (projectId != null) {
                notification.setProjectId(projectId);
            } else {
                throw new IllegalArgumentException("프로젝트 초대 알림에는 projectId가 필요합니다.");
            }
        }

        notificationRepository.save(notification);
        System.out.println("알림 저장 완료: " + notification.getId());
    }

    /**
     * 프로젝트 초대 알림을 생성하거나 업데이트
     * @param userId 사용자 ID
     * @param projectId 프로젝트 ID
     * @param message 알림 메시지
     */
    @Transactional
    public void createOrUpdateNotification(Long userId, Long projectId, String message) {
        // 기존 알림 조회
        Optional<Notification> existingNotification = notificationRepository.findByUserIdAndProjectIdAndType(
                userId, projectId, Notification.Type.PROJECT_INVITATION
        );

        if (existingNotification.isPresent()) {
            // 기존 알림 업데이트
            Notification notification = existingNotification.get();
            notification.setMessage(message);
            notification.setIsRead(false); // 읽음 상태 초기화
            notification.setCreatedAt(LocalDateTime.now()); // 타임스탬프 갱신
            notification.setIsActionCompleted(false); // 재처리 가능하도록 설정
            notificationRepository.save(notification);
        } else {
            createNotification(userId, "PROJECT_INVITATION", message, null, projectId);
        }
    }

}


