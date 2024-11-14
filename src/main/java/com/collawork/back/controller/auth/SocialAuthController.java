package com.collawork.back.controller.auth;

import com.collawork.back.service.auth.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/api/auth/social")
@CrossOrigin(origins = "http://localhost:3000")
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    @GetMapping("/{provider}")
    public RedirectView socialLogin(@PathVariable String provider, @RequestParam String code) {
        try {
            String token = socialAuthService.processSocialLogin(provider, code);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/main?token=" + token + "&provider=" + provider);
            System.out.println("발급된 토큰 : " + token);
            return redirectView;
        } catch (Exception e) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/login?error=" + e.getMessage());
            return redirectView;
        }
    }
}
