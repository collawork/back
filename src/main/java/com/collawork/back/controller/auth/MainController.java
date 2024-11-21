package com.collawork.back.controller.auth;

import com.collawork.back.dto.auth.UserDTO;
import com.collawork.back.model.auth.User;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class MainController {

    private final UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        UserDTO userDTO = new UserDTO(user, baseUrl);

        return ResponseEntity.ok(userDTO);
    }

    // 사용자 정보 업데이트
    @PutMapping("/user/update")
    public ResponseEntity<String> updateUserProfile(
            @RequestParam("username") String username,
            @RequestParam("company") String company,
            @RequestParam("position") String position,
            @RequestParam("fax") String fax,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setUsername(username);
            user.setCompany(company);
            user.setPosition(position);
            user.setFax(fax);

            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // 이미지 저장 로직 (예: 서버에 저장하거나 S3에 업로드)
                    String imagePath = saveProfileImage(profileImage);
                    user.setProfileImage(imagePath);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 실패");
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        String uploadDir = "C:/back/uploads/";
        String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }



    @GetMapping("/user/detail")
    public ResponseEntity<User> getUserByEmailOrId(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "currentUserId", required = false) Long currentUserId) {

        System.out.println("요청된 이메일: " + email);
        System.out.println("요청된 ID: " + id);
        System.out.println("현재 사용자 ID: " + currentUserId);

        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email);
        } else if (id != null) {
            user = userRepository.findById(id).orElse(null);
        }

        // 현재 로그인한 사용자를 검색 결과에서 제외
        if (user != null && user.getId().equals(currentUserId)) {
            System.out.println("로그인 사용자가 요청과 일치하여 결과에서 제외");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (user == null) {
            System.out.println("사용자를 찾을 수 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        System.out.println("응답 사용자 정보: " + user);
        return ResponseEntity.ok(user);
    }


}
