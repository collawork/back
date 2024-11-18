package com.collawork.back.service;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Project;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectParticipantsService {
    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public void addParticipant(Long projectId, Long userId, ProjectParticipant.Role role) {
        // ProjectParticipantId 객체 명시적 생성
        ProjectParticipantId id = new ProjectParticipantId(projectId, userId);

        // ProjectParticipant 엔티티 생성 및 매핑
        ProjectParticipant participant = new ProjectParticipant();
        participant.setId(id);
        participant.setRole(role);

        // Lazy Entity 매핑 (옵션: 필요에 따라 Entity 직접 초기화)
        participant.setProject(new Project(projectId));
        participant.setUser(new User(userId));

        // 엔티티 저장
        projectParticipantRepository.save(participant);
    }


}
