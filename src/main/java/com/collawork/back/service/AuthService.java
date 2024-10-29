package com.collawork.back.service;

import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/*
* 작성자 : 김동규 / 2024-10-29 최초작성
* 설명 : 사용자 회원가입 및 소셜 로직 구현
* */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    public void registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCompany(signupRequest.getCompany());
        user.setPosition(signupRequest.getPosition());
        user.setPhone(signupRequest.getPhone());
        user.setFax(signupRequest.getFax());
        userRepository.save(user);
    }

    public User registerOrLoginSocialUser(String provider, String token) {
        String email = fetchEmailFromProvider(provider, token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setOauthProvider(provider);
            userRepository.save(user);
        }
        return user;
    }

    private String fetchEmailFromProvider(String provider, String token) {
        String email = null;
        switch (provider.toLowerCase()) {
            case "google":
                email = fetchGoogleEmail(token);
                break;
            case "kakao":
                email = fetchKakaoEmail(token);
                break;
            case "naver":
                email = fetchNaverEmail(token);
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 소셜 로그인 제공자입니다: " + provider);
        }
        return email;
    }

    private String fetchGoogleEmail(String token) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(googleApiUrl)
                .queryParam("access_token", token);
        Map<String, Object> response = restTemplate.getForObject(builder.toUriString(), Map.class);
        return (String) response.get("email");
    }

    private String fetchKakaoEmail(String token) {
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        Map<String, Object> response = restTemplate.getForObject(
                kakaoApiUrl,
                Map.class,
                "Bearer " + token
        );
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    private String fetchNaverEmail(String token) {
        String naverApiUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(naverApiUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> naverResponse = (Map<String, Object>) response.getBody().get("response");

        return (String) naverResponse.get("email");
    }
}
