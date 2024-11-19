package com.collawork.back.controller;

import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.Voting;
import com.collawork.back.model.project.VotingContents;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectParticipantsService;
import com.collawork.back.service.ProjectService;
import com.collawork.back.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
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

        // `status='ACCEPTED'` 프로젝트만 조회
        List<String> projectList = projectService.selectAcceptedProjectTitlesByUserId(userId);
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
    public ResponseEntity<List<ProjectParticipant>> getAcceptedParticipants(@PathVariable Long projectId) {
        List<ProjectParticipant> participants = projectService.getAcceptedParticipants(projectId);
        return ResponseEntity.ok(participants);
    }






    // 투표 생성 메소드
    @PostMapping("newvoting")
    public  ResponseEntity<Object> votingInsert(
            @RequestParam("votingName") String votingName,
            @RequestParam("projectId") String projectId,
            @RequestParam("createdUser") String createdUser,
            @RequestParam("contents") List<String> contents,
            HttpServletRequest request){


        System.out.println("projectInformation 의 projectName : " + votingName);
        System.out.println("projectInformation 의 projectId : " + projectId);
        System.out.println("projectInformation 의 createdUser : " + createdUser);
        for(String content : contents){
            System.out.println("~!~!" + content);
        }
        System.out.println("~!~!" + contents);
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

        List<Voting> result = projectService.votingInsert(votingName,projectId,createdUser);
        System.out.println("projectInformation 결과 ::: " + result);

        if(result.size()>0) {
            System.out.println(result.stream().map(Voting::getId).collect(Collectors.toSet()).toString());
            System.out.println("votecontents 의 contents 값 :: " + contents);
            String listId = result.stream().map(Voting::getId).collect(Collectors.toSet()).toString();
            listId = listId.replaceAll("[\\[\\]]", "");
            // boolean result2 = projectService.insertVoteContents(contents, Long.valueOf(listId));
            System.out.println("listId :: " + listId);
              // System.out.println("result2 ::: " + result2);
            if(true){
                return ResponseEntity.ok("항목 저장에 성공.");
            }else{
                return ResponseEntity.status(403).body("투표 항목 저장중에 실패 ");
            }
        }else{
            return ResponseEntity.status(403).body("투표 생성 중 오류가 발생했습니다.");
        }

    }

//    @PostMapping("votecontents") // 항목 내용 저장
//    public ResponseEntity<Object> voteContentsSave(@RequestParam("contents") List<String> contents,
//                                                   @RequestParam("id") Long id,
//                                                   HttpServletRequest request){
//
//        System.out.println("votecontents 의 contents 값 :: " + contents);
//        System.out.println("votecontents 의 투표 고유 id 값 :: " + id);
//
//        String token = request.getHeader("Authorization");
//
//        System.out.println("token : " + token);
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
//        }
//        token = token.replace("Bearer ", "");
//        String email = jwtTokenProvider.getEmailFromToken(token);
//        if (email == null) {
//            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
//        }
//
//        boolean result = projectService.insertVoteContents(contents, id);
//        if(result){
//            return ResponseEntity.ok("항목 저장에 성공.");
//        }else{
//           return ResponseEntity.status(403).body("투표 항목 저장중에 실패 ");
//        }
//    }


}


