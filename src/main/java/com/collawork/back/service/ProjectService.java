package com.collawork.back.service;

import com.collawork.back.model.project.*;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.project.*;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.service.auth.SocialAuthService;
import com.collawork.back.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    @Autowired
    private VotingRecordRepository votingRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private VotingContentsRepository votingContentsRepository;

    @Autowired
    private NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Transactional
    public Long insertProject(String title, String context, Long userId, List<Long> participantIds) {
        // 프로젝트 엔터티 생성
        Project project = new Project();
        project.setProjectName(title);
        project.setCreatedBy(userId);
        project.setProjectCode(context);
        project.setCreatedAt(LocalDateTime.now());

        // 프로젝트 저장
        Project savedProject = projectRepository.save(project);

        // 프로젝트 생성자(ADMIN)의 참가 정보 추가
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        ProjectParticipant creatorParticipant = new ProjectParticipant();
        ProjectParticipantId creatorId = new ProjectParticipantId(savedProject.getId(), creator.getId());
        creatorParticipant.setId(creatorId);
        creatorParticipant.setProject(savedProject);
        creatorParticipant.setUser(creator);
        creatorParticipant.setRole(ProjectParticipant.Role.ADMIN); // ADMIN 역할 부여
        creatorParticipant.setStatus(ProjectParticipant.Status.ACCEPTED); // 상태를 ACCEPTED로 설정

        // 디버깅 로그 추가
        System.out.println("플젝 생성한사람 상태 : " + creatorParticipant.getStatus());

        // 생성자 정보 저장
        projectParticipantRepository.saveAndFlush(creatorParticipant);

        System.out.println("저장된 Creator Participant: " + creatorParticipant);
        System.out.println("Creator participant status: " + creatorParticipant.getStatus());

        // 초대된 사용자들에게 알림 및 참가 정보 추가
        if (participantIds != null) {
            for (Long participantId : participantIds) {
                // 생성자는 초대 대상에서 제외
                if (participantId.equals(userId)) continue;

                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                String message = "프로젝트 '" + title + "'에 초대되었습니다.";
                notificationService.createNotification(
                        participant.getId(),
                        "PROJECT_INVITATION",
                        message,
                        null,
                        savedProject.getId()
                );

                ProjectParticipant participantEntity = new ProjectParticipant();
                ProjectParticipantId participantEntityId = new ProjectParticipantId(savedProject.getId(), participant.getId());
                participantEntity.setId(participantEntityId);
                participantEntity.setProject(savedProject);
                participantEntity.setUser(participant);
                participantEntity.setRole(ProjectParticipant.Role.MEMBER); // 기본 역할은 MEMBER
                participantEntity.setStatus(ProjectParticipant.Status.PENDING); // 초대 상태는 PENDING

                System.out.println("참가자 상태 저장 전: " + participantEntity.getStatus());
                projectParticipantRepository.saveAndFlush(participantEntity);
            }
        }

        return savedProject.getId();
    }

    // 프로젝트 참여자 기반으로 이름 조회
    public List<String> selectProjectTitleByUserId(Long userId) {
      
        // Repository를 통해 프로젝트 목록 가져옴
        List<String> listTitle = projectParticipantRepository.findProjectTitlesByUserId(userId);
      
        System.out.println("ProjectService 의 listTitle: " + listTitle);

        if (listTitle == null || listTitle.isEmpty()) {
            return null; // 데이터 없으면 null
        }

        return listTitle;
    }

    /**
     * 프로젝트 참여자 목록 반환
     * */
    public List<Map<String, Object>> getParticipantsByUserId(Long userId) {
        List<Object[]> participantList = projectParticipantRepository.findParticipantsByUserId(userId);

        return participantList.stream()
                .map(row -> {
                    Map<String, Object> participant = new HashMap<>();
                    participant.put("name", row[0]);
                    participant.put("email", row[1]);
                    return participant;
                })
                .collect(Collectors.toList());
    }

    /**
     * 프로젝트 초대 로직
     * */
    public void inviteParticipant(Long projectId, Long userId, ProjectParticipant.Role role) {
        ProjectParticipantId participantId = new ProjectParticipantId(projectId, userId);

        ProjectParticipant participant = new ProjectParticipant();
        participant.setId(participantId);
        participant.setRole(role);
        participant.setStatus(ProjectParticipant.Status.PENDING);

        projectParticipantRepository.save(participant);
    }


    /**
     * 프로젝트 초대 승인 로직
     * */
    @Transactional
    public void acceptInvitation(Long projectId, Long userId) {
        ProjectParticipant participant = getParticipant(projectId, userId);
        log.debug("참가자 상태 변경 전: {}", participant.getStatus());
        if (!participant.getStatus().equals(ProjectParticipant.Status.PENDING)) {
            throw new IllegalStateException("이미 처리된 초대입니다.");
        }
        participant.setStatus(ProjectParticipant.Status.ACCEPTED);
        projectParticipantRepository.save(participant);
        log.debug("참가자 상태 변경 후: {}", participant.getStatus());
    }


    private ProjectParticipant getParticipant(Long projectId, Long userId) {
        ProjectParticipantId participantId = new ProjectParticipantId(projectId, userId);
        ProjectParticipant participant = projectParticipantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("참가자 정보를 찾을 수 없습니다."));
        log.debug("참가자 정보: {}", participant);
        return participant;
    }

    /**
     * 프로젝트 초대 거절 로직
     * */
    public void rejectInvitation(Long projectId, Long userId) {
        ProjectParticipant participant = getParticipant(projectId, userId);
        participant.setStatus(ProjectParticipant.Status.REJECTED);
        projectParticipantRepository.save(participant);
    }

    /**
     * 프로젝트에 초대된 모든 사용자 조회
     * */
    public List<ProjectParticipant> getAllParticipants(Long projectId) {
        return projectParticipantRepository.getAllParticipants(projectId);
    }

    /**
     * 프로젝트 초대에 승인한 사용자만 조회
     * */
    public List<ProjectParticipant> getAcceptedParticipants(Long projectId) {
        return projectParticipantRepository.getAcceptedParticipants(projectId);
    }

    public List<String> selectAcceptedProjectTitlesByUserId(Long userId) {
        // status='ACCEPTED' 조건으로 프로젝트 제목 조회
        return projectParticipantRepository.findAcceptedProjectsByUserId(userId);
    }

    public List<Map<String, Object>> selectAcceptedProjectsByUserId(Long userId) {
        return projectRepository.findAcceptedProjectsByUserId(userId).stream()
                .map(project -> {
                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("id", project.getId());
                    projectMap.put("name", project.getProjectName());
                    return projectMap;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPendingParticipants(Long projectId) {
        List<Object[]> participants = projectParticipantRepository.findPendingParticipantsByProjectId(projectId);

        return participants.stream()
                .map(row -> {
                    Map<String, Object> participant = new HashMap<>();
                    participant.put("username", row[0]); // 사용자 이름
                    participant.put("email", row[1]);   // 사용자 이메일
                    return participant;
                })
                .collect(Collectors.toList());
    }

    /**
     * id 로 유저 정보 조회(관리자)
     **/
    public Optional<User> selectUserNameByUserId(Long id) {

        Optional<User> userList = userRepository.findById(id);
        System.out.println("ProjectService 의 유저 정보 조회 : " + userList);
        return userList;

    }

    // ProjectName 으로 프로젝트 정보 조회
    public List<Project> selectByProjectName(String projectName) {

        List<Project> titleList = projectRepository.findByProjectName(projectName);
        System.out.println("ProjectService 의 selectByProjectName : " + titleList);
        return titleList;

    }


    public List<Voting> votingInsert(String votingName, String projectId, String createdUser, String detail) {

        Voting voting = new Voting();
        voting.setVotingName(votingName);
        voting.setProjectId(Long.valueOf(projectId));
        voting.setCreatedUser(createdUser);
        voting.setVotingDetail(detail);
        LocalDate localDate = LocalDate.now();
        voting.setCreatedAt(localDate.atStartOfDay());
        voting.setVote(true);

        List<Voting> result = Collections.singletonList(votingRepository.save(voting));


        return result;


    }

    public boolean insertVoteContents(List<String> contents, Long id) {

        for (String content : contents) {
            System.out.println("Service 의 contents :: " + content);

            // 매번 새로운 객체를 생성
            VotingContents votingContents = new VotingContents();
            votingContents.setVotingId(id);
            votingContents.setVotingContents(content);

            // 새로운 엔티티를 저장
            votingContentsRepository.save(votingContents);
        }
        return true;
    }

    // 프로젝트 id 에 해당되는 투표 정보 조회
    public List<Voting> findByVoting(Long projectId) {

        List<Voting> vote = votingRepository.findByProjectId(projectId);
        System.out.println("검색 후 받는 투표 정보들  :::  " + vote);
        return vote;

    }

    public List<VotingContents> findByVotingId(Long votingId) {

        List<VotingContents> contents = votingContentsRepository.findByVotingId(votingId);
        System.out.println("항목 검색 후 결과 :: " + contents);
        return contents;
    }

    public Boolean insertUserVote(Long votingId, Long contentsId, Long userId) {

        VotingRecord userVote = new VotingRecord();
        userVote.setVotingId(votingId);
        userVote.setUserId(userId);
        userVote.setContentsId(contentsId);

        List<VotingRecord> useVote = Collections.singletonList(votingRecordRepository.save(userVote));

        if(!useVote.isEmpty()){
            return true;
        }
        return false;
    }

    public List<VotingRecord> findByVotingIdRecord(Long votingId) {

        List<VotingRecord> uservoting = votingRecordRepository.findByVotingId(votingId);
        return uservoting;
    }

    public String getProjectNameById(Long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::getProjectName)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId));
    }
}
