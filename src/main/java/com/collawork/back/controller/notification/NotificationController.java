package com.collawork.back.controller.notification;

import com.collawork.back.model.notification.Notification;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Project;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.repository.notification.NotificationRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
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

    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    @Autowired
    private ProjectRepository projectRepository;

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
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 프로젝트 초대 승인/거절 처리
     * @param id 알림 ID
     * @param action "accept" 또는 "decline"
     * @return 처리 결과
     */
    @PostMapping("/{id}/respond")
    public ResponseEntity<String> respondToProjectInvitation(
            @PathVariable Long id,
            @RequestParam("action") String action) {

        // id 값이 null인 경우 예외 처리
        if (id == null) {
            return ResponseEntity.badRequest().body("알림 ID가 누락되었습니다.");
        }

        System.out.println("id : " + id);

        // 알림 조회
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 알림입니다.");
        }

        System.out.println("notification found: " + notification);
        System.out.println("Request ID in Notification: " + notification.getRequestId());

        // requestId가 null인 경우 처리
        if (notification.getRequestId() == null) {
            return ResponseEntity.badRequest().body("프로젝트 초대에 대한 유효한 요청 ID가 없습니다.");
        }

        // 알림이 프로젝트 초대인지 확인
        if (!"PROJECT_INVITATION".equals(notification.getType().name())) {
            return ResponseEntity.badRequest().body("유효하지 않은 초대 알림입니다.");
        }

        // 프로젝트 조회
        Project project = projectRepository.findById(notification.getRequestId())
                .orElse(null);
        if (project == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 프로젝트입니다.");
        }

        if ("accept".equalsIgnoreCase(action)) {
            // 초대 승인 -> 프로젝트에 사용자 추가
            ProjectParticipant participant = new ProjectParticipant();

            // 복합 키 설정
            ProjectParticipantId participantId = new ProjectParticipantId(
                    project.getId(), // projectId
                    notification.getUser().getId() // userId
            );
            participant.setId(participantId);

            participant.setProject(project); // 영속화된 프로젝트 객체 설정
            participant.setUser(notification.getUser()); // 사용자 설정
            participant.setRole(ProjectParticipant.Role.MEMBER); // 역할 설정 (기본값: MEMBER)
            projectParticipantRepository.save(participant);

            // 초대 승인 완료 처리
            notification.setIsActionCompleted(true);
            notification.setIsRead(true);
            notificationRepository.save(notification);

            return ResponseEntity.ok("프로젝트 초대가 승인되었습니다.");
        } else if ("decline".equalsIgnoreCase(action)) {
            // 초대 거절 -> 알림만 읽음 처리
            notification.setIsRead(true);
            notification.setIsActionCompleted(true);
            notificationRepository.save(notification);

            return ResponseEntity.ok("프로젝트 초대가 거절되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 행동입니다. '승인' 또는 '거절'을 사용하십시오.");
        }
    }



    /**
     * 알림 삭제
     * @param id 알림 ID
     * @return 처리 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 알림입니다.");
        }

        notificationRepository.delete(notification);
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }
}
