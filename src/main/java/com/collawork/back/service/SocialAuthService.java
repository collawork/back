package com.collawork.back.service;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SocialAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    public String processSocialLogin(String provider, String code) {
        String accessToken = getAccessTokenFromCode(provider, code);

        Map<String, String> userInfo = getSocialUserInfo(provider, accessToken);

        User user = registerOrLoginUser(userInfo, provider);

        return jwtTokenProvider.generateToken(user.getEmail());
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }


    private String getAccessTokenFromCode(String provider, String code) {
        String url;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (provider.equalsIgnoreCase("kakao")) {
            url = "https://kauth.kakao.com/oauth/token";
            params.add("grant_type", "authorization_code");
            params.add("client_id", "f851b2331a5966daafc3644d19ed1b77");
            params.add("redirect_uri", "http://localhost:8080/login/oauth2/code/kakao");
            params.add("code", code);
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 제공자입니다: " + provider);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        System.out.println("카카오 토큰 요청: URL=" + url + ", PARAMS=" + params);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            System.out.println("카카오 응답 코드: " + response.getStatusCode() + ", 본문: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                String accessToken = (String) body.get("access_token");
                if (accessToken != null) {
                    System.out.println("카카오에서 받은 액세스 토큰: " + accessToken);
                    return accessToken;
                } else {
                    System.out.println("Error: 액세스 토큰 없음, 응답 본문: " + body);
                    throw new RuntimeException("카카오로부터 액세스 토큰을 받지 못했습니다.");
                }
            } else {
                System.out.println("Error: 응답 코드 " + response.getStatusCode() + ", 응답 본문: " + response.getBody());
                throw new RuntimeException("카카오에서 유효한 응답을 받지 못했습니다.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP 오류 발생 - 상태 코드: " + e.getStatusCode() + ", 메시지: " + e.getResponseBodyAsString());
            throw new RuntimeException("카카오 액세스 토큰 요청 중 오류 발생: " + e.getMessage());
        }
    }




    private Map<String, String> getSocialUserInfo(String provider, String accessToken) {
        String url;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        if (provider.equalsIgnoreCase("google")) {
            url = "https://www.googleapis.com/oauth2/v3/userinfo";
        } else if (provider.equalsIgnoreCase("kakao")) {
            url = "https://kapi.kakao.com/v2/user/me";
        } else if (provider.equalsIgnoreCase("naver")) {
            url = "https://openapi.naver.com/v1/nid/me";
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 제공자입니다: " + provider);
        }

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return extractUserInfo(provider, responseBody);
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("소셜 제공자의 사용자 정보 요청 실패: " + e.getMessage());
        }
        throw new RuntimeException("소셜 제공자에서 사용자 정보를 가져올 수 없습니다.");
    }

    private Map<String, String> extractUserInfo(String provider, Map<String, Object> responseBody) {
        Map<String, String> userInfo = new HashMap<>();

        if (provider.equalsIgnoreCase("google")) {
            userInfo.put("email", Optional.ofNullable((String) responseBody.get("email")).orElse("unknown"));
            userInfo.put("name", Optional.ofNullable((String) responseBody.get("name")).orElse("unknown"));
        } else if (provider.equalsIgnoreCase("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            userInfo.put("email", Optional.ofNullable((String) kakaoAccount.get("email")).orElse("unknown"));
            userInfo.put("name", Optional.ofNullable((String) profile.get("nickname")).orElse("unknown"));
        } else if (provider.equalsIgnoreCase("naver")) {
            Map<String, Object> naverAccount = (Map<String, Object>) responseBody.get("response");
            userInfo.put("email", Optional.ofNullable((String) naverAccount.get("email")).orElse("unknown"));
            userInfo.put("name", Optional.ofNullable((String) naverAccount.get("name")).orElse("unknown"));
        }

        return userInfo;
    }


    private User registerOrLoginUser(Map<String, String> userInfo, String provider) {
        String email = userInfo.get("email");
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(userInfo.get("name"));
            user.setOauthProvider(provider);
            userRepository.save(user);
        }
        return user;
    }

}
