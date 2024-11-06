package com.collawork.back.controller;

import com.collawork.back.model.Project;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/projects")
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
            @RequestPart("title") String title,
            @RequestPart("text") String context){
        projectService.insertProject(title, context);
        System.out.println("test");
        return ResponseEntity.ok("프로젝트 생성이 완료되었습니다.");
    }
}
