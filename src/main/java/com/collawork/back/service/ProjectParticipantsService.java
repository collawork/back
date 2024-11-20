package com.collawork.back.service;

import com.collawork.back.controller.ProjectController;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Project;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectParticipantsService {

    @Autowired
    private ProjectParticipantRepository projectParticipantsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(ProjectParticipantsService.class);

    @Transactional
    public void addParticipant(Long projectId, Long userId, ProjectParticipant.Role role) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 복합 키 생성
        ProjectParticipantId participantId = new ProjectParticipantId(projectId, userId);

        // Participant 엔티티 생성
        ProjectParticipant participant = new ProjectParticipant();
        participant.setId(participantId);
        participant.setProject(project);
        participant.setUser(user);
        participant.setRole(role);

        // Role에 따른 Status 설정
        if (role == ProjectParticipant.Role.ADMIN) {
            participant.setStatus(ProjectParticipant.Status.ACCEPTED); // 관리자 역할은 ACCEPTED
        } else {
            participant.setStatus(ProjectParticipant.Status.PENDING); // 일반 멤버는 PENDING
        }

        log.debug("Saving participant: {}", participant);

        // 저장
        projectParticipantsRepository.save(participant);
    }

    public boolean isUserAdmin(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userEmail);
        }
        return projectParticipantsRepository
                .findByProjectIdAndUserId(projectId, user.getId())
                .map(participant -> participant.getRole() == ProjectParticipant.Role.ADMIN)
                .orElse(false);
    }

    // 초대 처리
    @Transactional
    public void inviteParticipants(Long projectId, List<Long> participantIds) {
        List<ProjectParticipant> existingParticipants = projectParticipantsRepository.findByProjectIdAndUserIdIn(projectId, participantIds);

        // REJECTED 상태 업데이트
        existingParticipants.stream()
                .filter(participant -> participant.getStatus() == ProjectParticipant.Status.REJECTED)
                .forEach(participant -> participant.setStatus(ProjectParticipant.Status.PENDING));

        List<Long> newParticipantIds = participantIds.stream()
                .filter(userId -> existingParticipants.stream()
                        .noneMatch(p -> p.getId().getUserId().equals(userId)))
                .collect(Collectors.toList());

        // 새로운 참가자 추가
        List<ProjectParticipant> newParticipants = newParticipantIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

                    return new ProjectParticipant(
                            new ProjectParticipantId(projectId, userId),
                            ProjectParticipant.Role.MEMBER
                    );
                })
                .collect(Collectors.toList());

        projectParticipantsRepository.saveAll(newParticipants);
    }

    // 이미 참여 중인 사용자 확인
    public List<Long> getAcceptedParticipantsIds(Long projectId, List<Long> participantIds) {
        return projectParticipantsRepository.findByProjectIdAndUserIdIn(projectId, participantIds).stream()
                .filter(participant -> participant.getStatus() == ProjectParticipant.Status.ACCEPTED)
                .map(participant -> participant.getId().getUserId())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> updateRejectedParticipantsToPending(Long projectId, List<Long> participantIds) {
        List<ProjectParticipant> rejectedParticipants = projectParticipantsRepository.findByProjectIdAndUserIdIn(projectId, participantIds).stream()
                .filter(participant -> participant.getStatus() == ProjectParticipant.Status.REJECTED)
                .collect(Collectors.toList());

        rejectedParticipants.forEach(participant -> participant.setStatus(ProjectParticipant.Status.PENDING));

        projectParticipantsRepository.saveAll(rejectedParticipants);

        return rejectedParticipants.stream()
                .map(participant -> participant.getUser().getId())
                .collect(Collectors.toList());
    }

}




