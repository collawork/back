package com.collawork.back.controller;

import com.collawork.back.dto.UserDTO;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class MainController {

    private final UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        UserDTO userDTO = new UserDTO(user, baseUrl);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/user/detail")
    public ResponseEntity<User> getUserByEmailOrId(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "currentUserId", required = false) Long currentUserId) {

        System.out.println("요청된 이메일: " + email);
        System.out.println("요청된 ID: " + id);
        System.out.println("현재 사용자 ID: " + currentUserId);

        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email);
        } else if (id != null) {
            user = userRepository.findById(id).orElse(null);
        }

        // 현재 로그인한 사용자를 검색 결과에서 제외
        if (user != null && user.getId().equals(currentUserId)) {
            System.out.println("로그인 사용자가 요청과 일치하여 결과에서 제외");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (user == null) {
            System.out.println("사용자를 찾을 수 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        System.out.println("응답 사용자 정보: " + user);
        return ResponseEntity.ok(user);
    }


}
