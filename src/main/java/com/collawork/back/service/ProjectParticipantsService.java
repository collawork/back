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
        // 프로젝트 및 사용자 엔티티 조회
        log.debug("Creating ProjectParticipant: projectId={}, userId={}, role={}", projectId, userId, role);

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

        // 저장
        projectParticipantsRepository.save(participant);
    }
}




