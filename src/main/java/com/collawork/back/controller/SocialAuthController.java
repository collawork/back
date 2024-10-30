package com.collawork.back.controller;

import com.collawork.back.service.SocialAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/login/oauth2/code")
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    // SocialAuthController.java
    @GetMapping("/{provider}")
    public void socialLogin(@PathVariable String provider, @RequestParam String code, HttpServletResponse response) throws IOException {
        String token = socialAuthService.handleSocialLogin(provider, code, null);
        response.sendRedirect("http://localhost:3000/social-login?token=" + token);
    }


}

