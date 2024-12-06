package com.collawork.back.controller.search;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Project;
import com.collawork.back.repository.ChatRoomRepository;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam("query") String query, @RequestParam("userId") Long userId) {
//        System.out.println("검색 쿼리: " + query);
//        System.out.println("사용자 ID: " + userId);

        // 사용자 검색
        List<User> users = userRepository.findByUsernameContaining(query);
//        System.out.println("검색된 사용자: " + users);

        // 프로젝트 검색
        List<Project> projects = projectRepository.findByProjectNameAndUserId(query, userId);
//        System.out.println("검색된 프로젝트: " + projects);

        SearchResult result = new SearchResult(users, projects);
        return ResponseEntity.ok(result);
    }


    static class SearchResult {
        private List<User> users;
        private List<Project> projects;


        public SearchResult(List<User> users, List<Project> projects) {
            this.users = users;
            this.projects = projects;
        }

        public List<User> getUsers() {
            return users;
        }

        public List<Project> getProjects() {
            return projects;
        }

    }
}
