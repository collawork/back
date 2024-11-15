package com.collawork.back.controller.search;

import com.collawork.back.model.User;
import com.collawork.back.model.Project;
import com.collawork.back.model.ChatRoom;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.repository.ProjectRepository;
import com.collawork.back.repository.ChatRoomRepository;
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

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam("query") String query) {
        // 사용자 검색
        List<User> users = userRepository.findByUsernameContaining(query);

        // 프로젝트 검색
        List<Project> projects = projectRepository.findByProjectNameContaining(query);

        // 채팅방 검색
        List<ChatRoom> chatRooms = chatRoomRepository.findByRoomNameContaining(query);

        SearchResult result = new SearchResult(users, projects, chatRooms);
        return ResponseEntity.ok(result);
    }

    static class SearchResult {
        private List<User> users;
        private List<Project> projects;
        private List<ChatRoom> chatRooms;

        public SearchResult(List<User> users, List<Project> projects, List<ChatRoom> chatRooms) {
            this.users = users;
            this.projects = projects;
            this.chatRooms = chatRooms;
        }

        public List<User> getUsers() {
            return users;
        }

        public List<Project> getProjects() {
            return projects;
        }

        public List<ChatRoom> getChatRooms() {
            return chatRooms;
        }
    }
}
