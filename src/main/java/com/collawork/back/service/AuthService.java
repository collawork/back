package com.collawork.back.service;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 작성자: 김동규 / 작성일: 2024.11.14
 * 설명: 사용자 인증 관련 service
 * */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads");
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
//        user.setUsername(generateUniqueUsername(signupRequest.getUsername()));   사용자 이름 중복검사 제거
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCompany(signupRequest.getCompany());
        user.setPosition(signupRequest.getPosition());
        user.setPhone(signupRequest.getPhone());
        user.setFax(signupRequest.getFax());

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
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = UPLOAD_DIR.resolve(uniqueFilename);

            profileImage.transferTo(filePath.toFile());

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 중 오류가 발생했습니다.", e);
        }
    }


    public Map<String, String> verifyAndProcessToken(String provider, String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = registerOrLoginSocialUser(provider, token);
        }

        Map<String, String> responseTokens = new HashMap<>();
        responseTokens.put("jwtToken", jwtTokenProvider.generateToken(email));
        responseTokens.put("email", email);

        return responseTokens;
    }

    public User registerOrLoginSocialUser(String provider, String token) {
        Map<String, String> userInfo = fetchUserInfoFromProvider(provider, token);
        String email = userInfo.get("email");
        String username = generateUniqueUsername(userInfo.get("name"));

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setOauthProvider(provider);
            userRepository.save(user);
        }

        return user;
    }

    private String generateUniqueUsername(String baseUsername) {
        String uniqueUsername = baseUsername;
        int counter = 1;

        while (userRepository.findByUsername(uniqueUsername) != null) {
            uniqueUsername = baseUsername + counter;
            counter++;
        }

        return uniqueUsername;
    }

    private Map<String, String> fetchUserInfoFromProvider(String provider, String token) {
        switch (provider.toLowerCase()) {
            case "google":
                return fetchGoogleUserInfo(token);
            case "kakao":
                return fetchKakaoUserInfo(token);
            case "naver":
                return fetchNaverUserInfo(token);
            default:
                throw new IllegalArgumentException("지원되지 않는 소셜 제공자입니다.");
        }
    }

    private Map<String, String> fetchKakaoUserInfo(String token) {
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoApiUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) ((Map<String, Object>) response.getBody().get("properties")).get("nickname");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("name", name);
        return userInfo;
    }

    private Map<String, String> fetchGoogleUserInfo(String token) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(googleApiUrl)
                .queryParam("access_token", token);
        Map<String, Object> response = restTemplate.getForObject(builder.toUriString(), Map.class);

        String email = (String) response.get("email");
        String name = (String) response.get("name");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("name", name);
        return userInfo;
    }

    private Map<String, String> fetchNaverUserInfo(String token) {
        String naverApiUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(naverApiUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> naverResponse = (Map<String, Object>) response.getBody().get("response");
        String email = (String) naverResponse.get("email");
        String name = (String) naverResponse.get("name");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("name", name);
        return userInfo;
    }
}
