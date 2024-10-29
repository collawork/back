package com.collawork.back.controller;

import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/register/social")
    public ResponseEntity<User> registerSocialUser(@RequestParam String provider, @RequestParam String token) {
        User user = authService.registerOrLoginSocialUser(provider, token);
        return ResponseEntity.ok(user);
    }
}