package com.collawork.back.repository.notification;

import com.collawork.back.model.notification.Notification;
import com.collawork.back.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);

    // 특정 사용자와 요청 ID에 해당하는 알림 목록을 찾기 위한 메서드
    List<Notification> findByUserAndRequestId(User user, Long requestId);

    Optional<Notification> findByUserIdAndProjectIdAndType(Long userId, Long projectId, Notification.Type type);
}