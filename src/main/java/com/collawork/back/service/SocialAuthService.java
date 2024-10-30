package com.collawork.back.service;

import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SocialAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> handleSocialLogin(String provider, String accessToken) {
        Map<String, String> userInfo = getSocialUserInfo(provider, accessToken);
        User user = registerOrLoginUser(userInfo);
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return Map.of("token", token);
    }

    private Map<String, String> getSocialUserInfo(String provider, String accessToken) {
        // 인증 제공자별 API URL
        String url = switch (provider.toLowerCase()) {
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
            case "facebook" -> "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 제공자입니다: " + provider);
        };

        Map<String, String> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("email")) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

        return response;
    }

    private User registerOrLoginUser(Map<String, String> userInfo) {
        String email = userInfo.get("email");
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(userInfo.get("name"));
            userRepository.save(user);
        }

        return user;
    }
}
