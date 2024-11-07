package com.collawork.back.controller;

import com.collawork.back.model.Project;
import com.collawork.back.model.User;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            @RequestBody Map<String, String> params, HttpServletRequest request){

        System.out.println(params);

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("인증 토큰이 없습니다.");
        }

        token = token.replace("Bearer ", "");

        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(403).body("유효하지 않은 토큰입니다.");
        }


//        System.out.println(title);
//        System.out.println(context);

        User user = new User();
        System.out.println(user.getId());
//        projectService.insertProject();
        System.out.println("test");
        return ResponseEntity.ok("프로젝트 생성이 완료되었습니다.");

    }
}
