package com.collawork.back.controller.notification;

import com.collawork.back.model.notification.Notification;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Project;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.repository.notification.NotificationRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

        // 알림 조회
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 알림입니다.");
        }

        // 알림 유형 확인
        if (!"PROJECT_INVITATION".equals(notification.getType().name())) {
            return ResponseEntity.badRequest().body("유효하지 않은 초대 알림입니다.");
        }

        // 프로젝트 조회 (notification.getProjectId()로 변경)
        Long projectId = notification.getProjectId();
        if (projectId == null) {
            return ResponseEntity.badRequest().body("프로젝트 ID가 누락되었습니다.");
        }

        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 프로젝트입니다.");
        }

        Long userId = notification.getUser().getId();
        ProjectParticipantId participantId = new ProjectParticipantId(project.getId(), userId);

        if ("accept".equalsIgnoreCase(action)) {
            // 참가자 조회
            Optional<ProjectParticipant> existingParticipant = projectParticipantRepository.findById(participantId);

            if (existingParticipant.isPresent()) {
                ProjectParticipant participant = existingParticipant.get();
                if (participant.getStatus() == ProjectParticipant.Status.ACCEPTED) {
                    return ResponseEntity.badRequest().body("사용자는 이미 프로젝트에 참여하고 있습니다.");
                }

                // 상태를 ACCEPTED로 변경
                participant.setStatus(ProjectParticipant.Status.ACCEPTED);
                projectParticipantRepository.save(participant);
            } else {
                // 새로운 참가자 생성
                ProjectParticipant participant = new ProjectParticipant();
                participant.setId(participantId);
                participant.setProject(project);
                participant.setUser(notification.getUser());
                participant.setRole(ProjectParticipant.Role.MEMBER);
                participant.setStatus(ProjectParticipant.Status.ACCEPTED);

                projectParticipantRepository.save(participant);
            }

            // 알림 처리
            notification.setIsActionCompleted(true);
            notification.setIsRead(true);
            notificationRepository.save(notification);

            return ResponseEntity.ok("프로젝트 초대가 승인되었습니다.");
        } else if ("decline".equalsIgnoreCase(action)) {
            // 초대 거절 처리
            notification.setIsRead(true);
            notification.setIsActionCompleted(true);
            notificationRepository.save(notification);

            return ResponseEntity.ok("프로젝트 초대가 거절되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 행동입니다.");
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
