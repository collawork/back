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

import java.util.List;
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
            @RequestParam("title") String title,
            @RequestParam("context") String context,
            @RequestParam("userId") Long userId,
            HttpServletRequest request){

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

        boolean result = projectService.insertProject(title,context, userId);
        String ret = null;
        if(result){
            ret = "프로젝트가 생성되었습니다.";
        }else{
            return ResponseEntity.status(403).body("프로젝트 생성 실패.");
        }
        return ResponseEntity.ok(ret);

    }

//    @PostMapping("/selectAll")
//    public ResponseEntity<String> selectAllProject(@RequestParam("userId") String userId,
//                                                   HttpServletRequest request){
//        System.out.println("userId" + userId);
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
//        List<String> menuName = projectService.selectProjectName(userId);
//
//        if(menuName.size() == 0){
//            return ResponseEntity.ok("생성한 프로젝트가 없습니다.");
//        }
//        return ResponseEntity.ok(menuName.toString()); // 프로젝트 name 리스트
//    }
}
