package com.collawork.back.controller.project;


import com.collawork.back.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
@RequestMapping("/api/project")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/newproject")
    public ResponseEntity<String> newProject(
            @RequestPart("name") String title,
            @RequestPart("text") String context){
        projectService.insertProject(title, context);
        return ResponseEntity.ok("프로젝트 생성이 완료되었습니다.");
    }

}
