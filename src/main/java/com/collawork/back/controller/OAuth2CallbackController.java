package com.collawork.back.controller;

import com.collawork.back.service.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/login/oauth2/code")
@CrossOrigin(origins = "http://localhost:3000")
public class OAuth2CallbackController {

    @Autowired
    private SocialAuthService socialAuthService;

    @GetMapping("/{provider}")
    public RedirectView handleOAuth2Callback(
            @PathVariable String provider,
            @RequestParam String code) {
        try {
            String accessToken = socialAuthService.processSocialLogin(provider, code);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/main?token=" + accessToken);
            return redirectView;

        } catch (Exception e) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/login?error=" + e.getMessage());
            return redirectView;
        }
    }
}
