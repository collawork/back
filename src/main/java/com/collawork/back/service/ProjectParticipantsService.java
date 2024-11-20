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

    public void inviteParticipants(Long projectId, List<Long> participantIds) {
        // 프로젝트 및 사용자 정보 확인
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<User> users = userRepository.findAllById(participantIds);

        if (users.isEmpty()) {
            throw new IllegalArgumentException("유효한 사용자 ID가 없습니다.");
        }

        // 초대 처리
        List<ProjectParticipant> participants = users.stream()
                .map(user -> new ProjectParticipant(
                        new ProjectParticipantId(projectId, user.getId()),
                        ProjectParticipant.Role.MEMBER
                ))
                .collect(Collectors.toList());

        projectParticipantsRepository.saveAll(participants);
    }

}




