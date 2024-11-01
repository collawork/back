package com.collawork.back.controller;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        String jwtToken = authService.login(loginRequest);
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    @PostMapping("/check-duplicates")
    public ResponseEntity<Map<String, Boolean>> checkDuplicates(@RequestBody Map<String, String> request) {
        boolean isDuplicate = authService.checkDuplicates(request);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestPart("signupRequest") SignupRequest signupRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        authService.register(signupRequest, profileImage);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

}
