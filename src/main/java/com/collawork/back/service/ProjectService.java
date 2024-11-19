package com.collawork.back.service;

import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.ProjectParticipantId;
import com.collawork.back.model.project.Voting;
import com.collawork.back.model.project.VotingContents;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import com.collawork.back.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import com.collawork.back.repository.project.VotingContentsRepository;
import com.collawork.back.repository.project.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private VotingContentsRepository votingContentsRepository;

    @Autowired
    private NotificationService notificationService;


    @Transactional
    public Long insertProject(String title, String context, Long userId, List<Long> participantIds) {
        Project project = new Project();
        project.setProjectName(title);
        project.setCreatedBy(userId);
        project.setProjectCode(context);
        project.setCreatedAt(LocalDateTime.now());

        // 명시적으로 영속성 컨텍스트에 추가
        entityManager.persist(project);

        // 프로젝트 저장
        Project savedProject = projectRepository.save(project);

        // 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 명시적으로 영속성 컨텍스트에 추가
        entityManager.persist(user);

        // ID를 명시적으로 설정
        ProjectParticipantId participantId = new ProjectParticipantId(savedProject.getId(), user.getId());
        ProjectParticipant creator = new ProjectParticipant();
        creator.setId(participantId);
        creator.setProject(savedProject);
        creator.setUser(user);
        creator.setRole(ProjectParticipant.Role.ADMIN);

        // 프로젝트 생성자를 저장
        projectParticipantRepository.save(creator);

        // 초대된 사용자들에게 알림 생성
        if (participantIds != null) {
            for (Long participantIdValue : participantIds) {
                // 생성자는 초대 목록에서 제외
                if (participantIdValue.equals(userId)) {
                    continue;
                }

                User participant = userRepository.findById(participantIdValue)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // participant 객체 유효성 검증
                if (participant == null || participant.getId() == null) {
                    throw new IllegalArgumentException("유효하지 않은 사용자입니다.");
                }

                // 알림 메시지 생성
                String message = "프로젝트 '" + title + "'에 초대되었습니다.";
                notificationService.createNotification(
                        participant.getId(),        // 사용자 ID
                        "PROJECT_INVITATION",      // 알림 타입
                        message,                   // 알림 메시지
                        null,                      // requestId는 null
                        savedProject.getId()       // projectId 전달
                );
            }
        }

        return savedProject.getId();
    }





    // id 로 프로젝트 이름 조회
    public List<String> selectProjectTitleByUserId(Long userId) {

        List<Project> titleList = projectRepository.findByCreatedBy(userId);
        System.out.println("ProjectService 의 titleList : " +titleList);
        List<String> listTitle = titleList.stream().map(Project::getProjectName).collect(toList());
        System.out.println("ProjectService 의 listTitle" + listTitle);
        if(titleList .isEmpty()){
            return null;
        }else{
            return listTitle;
        }

    }


    // id 로 유저 정보 조회(관리자)
    public Optional<User> selectUserNameByUserId(Long id) {

        Optional<User> userList = userRepository.findById(id);
        System.out.println("ProjectService 의 유저 정보 조회 : " + userList);
        return userList;

}

    // ProjectName 으로 프로젝트 정보 조회
    public List<Project> selectByProjectName(String projectName) {

        List<Project> titleList = projectRepository.findByProjectName(projectName);
        System.out.println("ProjectService 의 selectByProjectName : " +titleList);
        return titleList;

    }


    public List<Voting> votingInsert(String votingName, String projectId, String createdUser) {

        Voting voting = new Voting();
        voting.setVotingName(votingName);
        voting.setProjectId(Long.valueOf(projectId));
        voting.setCreatedUser(createdUser);
        LocalDate localDate = LocalDate.now();
        voting.setCreatedAt(localDate.atStartOfDay());
        voting.setVote(true);

        List<Voting> result = Collections.singletonList(votingRepository.save(voting));


        return result;


     }

    public boolean insertVoteContents(List<String> contents, Long id) {

        for(String con : contents){
            System.out.println("service 의 contents :: " + con);
        }

        VotingContents votingContents = new VotingContents();
        votingContents.setVotingId(id);
        for (String content : contents) {
            votingContents.setVotingContents(content);
            votingContentsRepository.save(votingContents);
        }
        return true;
    }
}
