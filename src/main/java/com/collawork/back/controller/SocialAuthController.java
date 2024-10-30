package com.collawork.back.controller;

import com.collawork.back.service.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/social")
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    @PostMapping("/{provider}")
    public ResponseEntity<?> socialLogin(@PathVariable String provider, @RequestParam String token) {
        return ResponseEntity.ok(socialAuthService.handleSocialLogin(provider, token));
    }
}
