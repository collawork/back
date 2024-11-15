package com.collawork.back.controller;

import com.collawork.back.model.project.Project;
import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Voting;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestParam("title") String title,
            @RequestParam("context") String context,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        System.out.println("request : " + request);
        System.out.println("params : " + title);
        System.out.println("context : " + context);
        System.out.println("userId : " + userId);

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

        boolean result = projectService.insertProject(title, context, userId);
        String ret = null;
        if (result) {
            ret = "프로젝트가 생성되었습니다.";
        } else {
            return ResponseEntity.status(403).body("프로젝트 생성 실패.");
        }
        return ResponseEntity.ok(ret);

    }

    @PostMapping("/selectAll")
    public ResponseEntity<Object> getProjectTitle(@RequestParam("userId") String userId,
                                                  HttpServletRequest request) {
        System.out.println("selectAll 의 userId : " + userId);

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

        List<String> projectList = projectService.selectProjectTitleByUserId(Long.valueOf(userId));
        System.out.println("projectController-selectAll: " + projectList);

        if (projectList.isEmpty()) {
            return ResponseEntity.ok("생성한 프로젝트가 없습니다.");
        }
        return ResponseEntity.ok(projectList); // 프로젝트 이름 리스트
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


