package com.collawork.back.controller;

import com.collawork.back.dto.project.ProjectRequestDTO;
import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.model.project.Voting;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectParticipantsService;
import com.collawork.back.service.ProjectService;
import com.collawork.back.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            @RequestBody ProjectRequestDTO requestData,
            HttpServletRequest request) {

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
            String title = requestData.getTitle();
            String context = requestData.getContext();
            Long userId = requestData.getUserId();
            List<Long> participants = requestData.getParticipants();

            // 1. 프로젝트 생성
            Long projectId = projectService.insertProject(title, context, userId);
            if (projectId == null) {
                return ResponseEntity.status(403).body("프로젝트 생성 실패.");
            }

            // 2. 생성자를 ADMIN으로 project_participants에 추가
            projectParticipantsService.addParticipant(
                    projectId, userId, ProjectParticipant.Role.ADMIN);

            // 3. 초대받은 사용자들을 MEMBER로 추가
            for (Long participantId : participants) {
                projectParticipantsService.addParticipant(
                        projectId, participantId, ProjectParticipant.Role.MEMBER);
            }

            return ResponseEntity.ok("프로젝트가 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("요청 데이터 처리 중 오류 발생: " + e.getMessage());
        }
    }




    @PostMapping("/selectAll")
    public ResponseEntity<Object> getProjectTitle(@RequestBody Map<String, Object> requestBody,
                                                  HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }

        Long userId;
        try {
            userId = Long.valueOf(requestBody.get("userId").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("userId 형식이 잘못되었습니다.");
        }

        // 프로젝트 목록 조회
        List<String> projectList = projectService.selectProjectTitleByUserId(userId);
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

    // 투표 생성 메소드
    @PostMapping("newvoting")
    public ResponseEntity<Object> votingInsert(
            @RequestParam("votingName") String votingName,
            @RequestParam("projectId") String projectId,
            @RequestParam("created_user") String createdUser,
            HttpServletRequest request){

        System.out.println("projectInformation 의 projectName : " + votingName);
        System.out.println("projectInformation 의 projectId : " + projectId);
        System.out.println("projectInformation 의 createdUser : " + createdUser);

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

        boolean result = projectService.votingInsert(votingName,projectId,createdUser);
        if (result) {
              return ResponseEntity.ok("투표가 생성되었습니다 !");
        }else{
            return ResponseEntity.status(405).body("투표 생성에 실패하였습니다 !");
        }



    }


}


