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
            // SocialAuthService를 사용해 받은 인가 코드를 엑세스 토큰으로 교환
            String accessToken = socialAuthService.processSocialLogin(provider, code);

            // 인증이 성공한 경우 /main 페이지로 리디렉트하며, 토큰을 쿼리 파라미터로 전달
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/main?token=" + accessToken);
            return redirectView;

        } catch (Exception e) {
            // 오류 발생 시 /login 페이지로 리디렉트하며 에러 메시지를 쿼리 파라미터로 전달
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000/login?error=" + e.getMessage());
            return redirectView;
        }
    }
}
