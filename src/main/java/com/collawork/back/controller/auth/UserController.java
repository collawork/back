package com.collawork.back.controller.auth;

import com.collawork.back.dto.auth.LoginRequest;
import com.collawork.back.dto.auth.SignupRequest;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 로그인 요청 처리 후 JWT 토큰 생성
        String jwtToken = authService.login(loginRequest);
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getEmail());
        System.out.println("jwtToken : " + jwtToken);

        // 사용자 ID 조회
        Long userId = userRepository.findByEmail(loginRequest.getEmail()).getId();

        // 응답에 token, refreshToken, userId를 포함하여 반환
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", userId);

        return ResponseEntity.ok(response);
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
            String token = refreshToken.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmailFromToken(token);

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
