package com.collawork.back.controller;

import com.collawork.back.model.User;
import com.collawork.back.service.AuthService;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login/oauth2")
@CrossOrigin(origins = "http://localhost:3000")
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    @GetMapping("/code/{provider}")
    public ResponseEntity<?> socialLogin(
            @PathVariable String provider,
            @RequestParam String code) {
        try {
            String token = socialAuthService.processSocialLogin(provider, code);
            return ResponseEntity.ok().body(Map.of(
                    "token", token,
                    "message", "Successfully authenticated with " + provider
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}
