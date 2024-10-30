package com.collawork.back.controller;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestPart("signupRequest") SignupRequest signupRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            authService.registerUser(signupRequest, profileImage);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("프로필 이미지 업로드 실패");
        }
    }

    @PostMapping("/register/social")
    public ResponseEntity<User> registerSocialUser(@RequestParam String provider, @RequestParam String token) {
        User user = authService.registerOrLoginSocialUser(provider, token);
        return ResponseEntity.ok(user);
    }

    // 중복 검사 엔드포인트
    @PostMapping("/check-duplicates")
    public ResponseEntity<Map<String, String>> checkDuplicates(@RequestBody SignupRequest signupRequest) {
        Map<String, String> errors = new HashMap<>();

        if (authService.isUsernameTaken(signupRequest.getUsername())) {
            errors.put("username", "이미 존재하는 이름입니다.");
        }
        if (authService.isEmailTaken(signupRequest.getEmail())) {
            errors.put("email", "이미 사용 중인 이메일입니다.");
        }
        if (authService.isPhoneTaken(signupRequest.getPhone())) {
            errors.put("phone", "이미 사용 중인 핸드폰 번호입니다.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok().build();
    }
}
