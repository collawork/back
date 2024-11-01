package com.collawork.back.controller;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok("성공적으로 회원 가입이 완료 되었습니다.");
    }

    @PostMapping("/register/social")
    public ResponseEntity<User> registerSocialUser(@RequestParam String provider, @RequestParam String token) {
        User user = authService.registerOrLoginSocialUser(provider, token);
        return ResponseEntity.ok(user);
    }

    // 중복 검사 엔드포인트 추가
    @PostMapping("/check-duplicates")
    public ResponseEntity<Map<String, String>> checkDuplicates(@RequestBody SignupRequest signupRequest) {
        Map<String, String> errors = new HashMap<>();

        // 이름 중복 검사
        if (authService.isUsernameTaken(signupRequest.getUsername())) {
            errors.put("username", "이미 존재하는 이름입니다.");
        }

        // 이메일 중복 검사
        if (authService.isEmailTaken(signupRequest.getEmail())) {
            errors.put("email", "이미 사용 중인 이메일입니다.");
        }

        // 핸드폰 번호 중복 검사
        if (authService.isPhoneTaken(signupRequest.getPhone())) {
            errors.put("phone", "이미 사용 중인 핸드폰 번호입니다.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors); // 중복된 경우 에러 응답
        }

        return ResponseEntity.ok().build(); // 중복이 없는 경우 성공 응답
    }

}
