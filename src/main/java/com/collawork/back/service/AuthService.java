package com.collawork.back.service;

import com.collawork.back.dto.LoginRequest;
import com.collawork.back.dto.SignupRequest;
import com.collawork.back.model.User;
import com.collawork.back.repository.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return jwtTokenProvider.generateToken(user.getEmail());
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public void registerUser(SignupRequest signupRequest, MultipartFile profileImage) throws IOException {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setCompany(signupRequest.getCompany());
        user.setPosition(signupRequest.getPosition());
        user.setPhone(signupRequest.getPhone());
        user.setFax(signupRequest.getFax());

        // 프로필 이미지 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImagePath = saveProfileImage(profileImage);
            user.setProfileImage(profileImagePath);
        }

        userRepository.save(user);
    }

    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        Files.createDirectories(fileStorageLocation);
        String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
        Path targetLocation = fileStorageLocation.resolve(fileName);
        Files.copy(profileImage.getInputStream(), targetLocation);

        return "/uploads/" + fileName;
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
        switch (provider.toLowerCase()) {
            case "google": return fetchGoogleEmail(token);
            case "kakao": return fetchKakaoEmail(token);
            case "naver": return fetchNaverEmail(token);
            default: throw new IllegalArgumentException("지원되지 않는 소셜 로그인 제공자입니다: " + provider);
        }
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
        Map<String, Object> response = restTemplate.getForObject(kakaoApiUrl, Map.class, "Bearer " + token);
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

    public boolean isUsernameTaken(String username) { return userRepository.findByUsername(username) != null; }
    public boolean isEmailTaken(String email) { return userRepository.findByEmail(email) != null; }
    public boolean isPhoneTaken(String phone) { return userRepository.findByPhone(phone) != null; }
}
