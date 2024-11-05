package com.collawork.back.controller;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken.replace("Bearer ", ""));

            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(403).body("사용자를 찾을 수 없습니다.");
            }
            String newAccessToken = jwtTokenProvider.generateToken(email);

            return ResponseEntity.ok(Map.of("token", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(403).body("유효하지 않은 리프레시 토큰입니다.");
        }
    }


}
