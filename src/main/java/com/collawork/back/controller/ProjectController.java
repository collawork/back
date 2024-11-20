package com.collawork.back.controller;

import com.collawork.back.model.project.*;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectParticipantsService;
import com.collawork.back.service.ProjectService;
import com.collawork.back.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectParticipantsService projectParticipantsService;

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);


    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectInfo(@PathVariable Long projectId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        Project project = projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(project);
    }

    @PostMapping("/newproject")
    public ResponseEntity<String> newProject(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {

        log.debug("Received request data: {}", requestData);

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        try {
            // 요청 데이터 파싱
            String title = (String) requestData.get("title");
            String context = (String) requestData.get("context");
            Long userId = Long.valueOf(requestData.get("userId").toString());
            List<Long> participants = ((List<?>) requestData.get("participants")).stream()
                    .map(participant -> Long.valueOf(participant.toString()))
                    .toList();

            log.debug("Project ID created: {}", userId);

            // 프로젝트 생성
            Long projectId = projectService.insertProject(title, context, userId, participants);

            // ADMIN 역할로 생성자 추가
            projectParticipantsService.addParticipant(projectId, userId, ProjectParticipant.Role.ADMIN);

            // MEMBER 역할로 참가자 추가
            for (Long participantId : participants) {
                projectParticipantsService.addParticipant(projectId, participantId, ProjectParticipant.Role.MEMBER);
            }

            return ResponseEntity.ok("프로젝트가 생성되었습니다.");
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body("요청 데이터 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/selectAll")
    public ResponseEntity<Object> getProjectTitle(@RequestBody Map<String, Object> requestBody,
                                                  HttpServletRequest request) {
        System.out.println("요청 바디: " + requestBody);

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        System.out.println("추출한 이메일: " + email);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        Long userId;
        try {
            userId = Long.valueOf(requestBody.get("userId").toString());
            System.out.println("추출한 userId: " + userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("userId 형식이 잘못되었습니다.");
        }

        // 프로젝트 ID와 이름을 모두 조회
        List<Map<String, Object>> projectList = projectService.selectAcceptedProjectsByUserId(userId);

        System.out.println("조회된 프로젝트 목록: " + projectList);

        if (projectList == null || projectList.isEmpty()) {
            return ResponseEntity.ok("생성한 프로젝트가 없습니다.");
        }

        return ResponseEntity.ok(projectList);
    }





    @PostMapping("/projecthomeusers") // 유저 정보 조회
    public ResponseEntity<Object> getProjectHome(@RequestParam("id") Long userId, HttpServletRequest request) {

        System.out.println("projectHome 의 userId : " + userId);

        String token = request.getHeader("Authorization");

        System.out.println("token : " + token);
        System.out.println("플젝의 userId : " + userId);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }
        System.out.println("selectAll");

        // 프로젝트 생성자의 정보 조회
    Optional<User> users = projectService.selectUserNameByUserId(Long.valueOf(userId));

        if (users.isEmpty()) {
            return ResponseEntity.ok("프로젝트 생성자의 정보가 없습니다.");
        }
        return ResponseEntity.ok(users); // 프로젝트 정보 리스트.
    }

    // 프로젝트 전체 조회 메소드
    @PostMapping("projectselect")
    public ResponseEntity<Object> getProjectSelect(@RequestParam("projectName") String projectName,
                                                   HttpServletRequest request) {

        System.out.println("projectInformation 의 projectName : " + projectName);

        String token = request.getHeader("Authorization");

        System.out.println("token : " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }
        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }
        System.out.println("selectAll");

        // projectName 으로 project 엔티티 조회 후 가져옴
        List<Project> projectList = projectService.selectByProjectName(projectName);
        System.out.println("projectController 의 프로젝트 정보 조회 ::: " + projectList);
        if (projectList.isEmpty()) {
            return ResponseEntity.status(403).body("조회된 정보가 없습니다.");
        }
        return ResponseEntity.ok(projectList);
    }

    /**
     * 프로젝트 참여자 조회 메소드
     * parmas - userId
     * */
    @PostMapping("/participants")
    public ResponseEntity<List<Map<String, Object>>> getProjectParticipants(@RequestBody Map<String, Object> requestBody,
                                                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        Long userId = Long.valueOf(requestBody.get("userId").toString());

        // 참여자 목록 조회
        List<Map<String, Object>> participants = projectService.getParticipantsByUserId(userId);

        return ResponseEntity.ok(participants);
    }

    @PostMapping("/{projectId}/accept")
    public ResponseEntity<String> acceptInvitation(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        projectService.acceptInvitation(projectId, userId);
        return ResponseEntity.ok("프로젝트 초대 승인");
    }

    @PostMapping("/{projectId}/reject")
    public ResponseEntity<String> rejectInvitation(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        projectService.rejectInvitation(projectId, userId);
        return ResponseEntity.ok("프로젝트 초대 거절");
    }

    /**
     * 프로젝트 모든 참가자 조회
     * */
    @GetMapping("/{projectId}/participants")
    public ResponseEntity<List<ProjectParticipant>> getAllParticipants(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getAllParticipants(projectId));
    }

    /**
     * 프로젝트 승인한 참가자만 조회
     * */
    @PostMapping("/{projectId}/participants/{userId}/accept")
    public ResponseEntity<String> acceptParticipant(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        log.debug("초대 승인 요청: projectId={}, userId={}", projectId, userId);
        projectService.acceptInvitation(projectId, userId);
        return ResponseEntity.ok("참가 요청이 승인되었습니다.");
    }

    /**
     * 프로젝트 승인된 참가자만 조회(userId 안받고)
     */
    @GetMapping("/{projectId}/participants/accepted")
    public ResponseEntity<List<Map<String, Object>>> getAcceptedParticipants(@PathVariable Long projectId) {
        List<ProjectParticipant> participants = projectService.getAcceptedParticipants(projectId);

        System.out.println("프로젝트 참여자 조회 시 projectId : " + projectId);

        // 사용자 정보 포함 여부 확인
        List<Map<String, Object>> formattedParticipants = participants.stream()
                .map(participant -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", participant.getUser().getId());
                    map.put("username", participant.getUser().getUsername());
                    map.put("email", participant.getUser().getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(formattedParticipants);
    }

    /**
     * 프로젝트에 초대된 사용자 조회(승인, 거절 전)
     * */
    @GetMapping("/{projectId}/participants/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingParticipants(
            @PathVariable Long projectId,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        // PENDING 상태인 사람만 조회
        List<Map<String, Object>> pendingParticipants = projectService.getPendingParticipants(projectId);

        if (pendingParticipants.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(pendingParticipants);
    }



    // 투표 생성 메소드
    @PostMapping("newvoting")
    public  ResponseEntity<Object> votingInsert(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request){

        String votingName = (String) payload.get("votingName");
        String projectId = String.valueOf(payload.get("projectId"));
        String createdUser = String.valueOf(payload.get("createdUser"));
        String detail = (String) payload.get("detail");
        List<String> contents = (List<String>) payload.get("contents");

        System.out.println("Voting Name: " + votingName);
        System.out.println("Project ID: " + projectId);
        System.out.println("Created User: " + createdUser);
        contents.forEach(content -> System.out.println("Content: " + content));

        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        List<Voting> result = projectService.votingInsert(votingName,projectId,createdUser,detail);

        if(result.size()>0) {
            System.out.println(result.stream().map(Voting::getId).collect(Collectors.toSet()).toString());
            System.out.println("votecontents 의 contents 값 :: " + contents);
            String listId = result.stream().map(Voting::getId).collect(Collectors.toSet()).toString();
            listId = listId.replaceAll("[\\[\\]]", "");
            boolean result2 = projectService.insertVoteContents(contents, Long.valueOf(listId));
            System.out.println("listId :: " + listId);
              System.out.println("result2 ::: " + result2);
            if(true){
                return ResponseEntity.ok("항목 저장에 성공.");
            }else{
                return ResponseEntity.status(403).body("투표 항목 저장중에 실패 ");
            }
        }else{
            return ResponseEntity.status(403).body("투표 생성 중 오류가 발생했습니다.");
        }

    }

    @PostMapping("findVoting") // 투표 기본 정보 불러오기
    public ResponseEntity<Object> findVoting(
            @RequestParam("projectId") Long projectId,
            HttpServletRequest request){

        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        System.out.println("findVoting 의 받아온 projectId :: " + projectId);
        List<Voting> vote =  projectService.findByVoting(projectId);
        System.out.println("vote :: " + vote);
        if(vote.isEmpty()){
            return ResponseEntity.status(404).body(vote);
        }else{

            return ResponseEntity.ok(vote);
        }

    }

    @PostMapping("findContents") // 투표 contents 불러오기
    public ResponseEntity<Object> findContents(
            @RequestParam("votingId") Long votingId,
            HttpServletRequest request){

        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }
        System.out.println("받아오는 vote 고유 id : :" + votingId);

        List<VotingContents> contents = projectService.findByVotingId(votingId);
        System.out.println("contents :: " + contents);
        if(contents.isEmpty()){
            return ResponseEntity.status(404).body(contents);
        }else{
            return ResponseEntity.ok(contents);
        }
    }


    // 유저가 투표한 투표 항목 (voting_record 테이블) insert
    @PostMapping("uservoteinsert")
    public ResponseEntity<Object> findUserVoting(
            @RequestParam("votingId") Long votingId, // 투표 고유 id
            @RequestParam("contentsId") Long contentsId, // 투표 한 항목 id
            @RequestParam("userId") Long userId, // user 고유 id
            HttpServletRequest request){

        System.out.println("넘어온 유저 투표 정보 :: " + votingId);
        System.out.println("넘어온 유저 투표 정보 :: " + contentsId);
        System.out.println("넘어온 유저 투표 정보 :: " + userId);

        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }
        System.out.println("유저 투표 받아오는 정보 ::: "+votingId+contentsId+userId);

        Boolean userVote = projectService.insertUserVote(votingId,contentsId,userId);
        if(userVote){
            return ResponseEntity.ok("유저 투표 정보 등록 성공.");
        }else{
            return ResponseEntity.status(404).body("유저 투표 정보 등록 실패.");

        }
    }

    // 투표 별 유저가 투표한 항목 불러오기
    @PostMapping("findUserVoting")
    public ResponseEntity<Object> userVoteInsert(
            @RequestParam("votingId") Long votingId, // 투표 고유 id
            @RequestParam("userId") Long userId, // 투표 한 항목 id
            HttpServletRequest request){

        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        List<VotingRecord> uservoting = projectService.findByVotingIdRecord(votingId);
        System.out.println(uservoting);

        if(uservoting.isEmpty()){
            return ResponseEntity.status(404).body(uservoting);
        }else{
            System.out.println(uservoting);

            // 투표의 고유 id 를 가지고 온다.
            // 이제 가지고 해당 userId 가 있는지 확인한다.
            uservoting.getClass();
            uservoting.toString();
            System.out.println("확인 :: " + uservoting);
            System.out.println("확인 :: " + uservoting.toString());
            int please = 0;

            for(VotingRecord user: uservoting) {
                if (user.getUserId().equals(userId)) {
                    List<Long> userVote = new ArrayList<>();
                    userVote.add(user.getVotingId());
                    userVote.add(user.getContentsId());
                    please = 1;
                    System.out.println(userVote.toString());
                    System.out.println("please :: " + please);
                    // 해당 유저 id 가 있으면(투표한 정보) 투표Id, 투표 항목 반환
                    return ResponseEntity.ok(userVote.toString());
                    // return user.getContentsId();
                }else{
                    System.out.println("user 정보가 없어서 여기 탐");
                    please = 2;
                }
            }
            // 해당 유저 id 가 없다면(투표한 정보) null 반환
            System.out.println("please:: " + please);
            return ResponseEntity.ok(null);


        }
    }
}


