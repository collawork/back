package com.collawork.back.service;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads");

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 유효하지 않습니다.");
        }
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    public boolean checkDuplicates(Map<String, String> request) {
        String email = request.get("email");
        String username = request.get("username");
        String phone = request.get("phone");

        Optional<User> userByEmail = email != null ? Optional.ofNullable(userRepository.findByEmail(email)) : Optional.empty();
        Optional<User> userByUsername = username != null ? Optional.ofNullable(userRepository.findByUsername(username)) : Optional.empty();
        Optional<User> userByPhone = phone != null ? Optional.ofNullable(userRepository.findByPhone(phone)) : Optional.empty();
        return userByEmail.isPresent() || userByUsername.isPresent() || userByPhone.isPresent();
    }

    public void register(SignupRequest signupRequest, MultipartFile profileImage) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCompany(signupRequest.getCompany());
        user.setPosition(signupRequest.getPosition());
        user.setPhone(signupRequest.getPhone());
        user.setFax(signupRequest.getFax());

        // 이미지 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String imagePath = saveProfileImage(profileImage);
            user.setProfileImage(imagePath);
        }

        userRepository.save(user);
    }

    private String saveProfileImage(MultipartFile profileImage) {
        try {
            if (Files.notExists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }

            String originalFilename = profileImage.getOriginalFilename();
            String filePath = UPLOAD_DIR.resolve(System.currentTimeMillis() + "_" + originalFilename).toString();
            profileImage.transferTo(new File(filePath));

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    public Map<String, String> verifyAndProcessToken(String provider, String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        // JWT 유효성 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        // 새로운 소셜 사용자 등록 또는 기존 사용자 로그인 처리
        if (user == null) {
            user = registerOrLoginSocialUser(provider, token);
        }

        Map<String, String> responseTokens = new HashMap<>();
        responseTokens.put("jwtToken", jwtTokenProvider.generateToken(email));
        responseTokens.put("email", email);

        return responseTokens;
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
        // 기존에 구현된 fetchKakaoEmail, fetchGoogleEmail 등을 호출합니다.
        switch (provider.toLowerCase()) {
            case "google":
                return fetchGoogleEmail(token);
            case "kakao":
                return fetchKakaoEmail(token);
            case "naver":
                return fetchNaverEmail(token);
            default:
                throw new IllegalArgumentException("지원되지 않는 소셜 제공자입니다.");
        }
    }

    // 소셜 제공자의 이메일 요청 메소드들
    private String fetchKakaoEmail(String token) {
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoApiUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    private String fetchGoogleEmail(String token) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(googleApiUrl)
                .queryParam("access_token", token);
        Map<String, Object> response = restTemplate.getForObject(builder.toUriString(), Map.class);
        return (String) response.get("email");
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
