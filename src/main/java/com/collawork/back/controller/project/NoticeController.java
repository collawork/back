package com.collawork.back.controller.project;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.project.Notice;
import com.collawork.back.model.project.NoticeRequest;
import com.collawork.back.model.project.ProjectParticipant;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.project.NoticeRepository;
import com.collawork.back.repository.project.ProjectParticipantRepository;
import com.collawork.back.repository.project.ProjectRepository;
import com.collawork.back.utils.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ProjectParticipantRepository participantRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // 특정 프로젝트의 공지사항 목록 조회
    @GetMapping("/{projectId}/notices")
    public List<Notice> getNotices(@PathVariable Long projectId) throws NotFoundException {
        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("프로젝트를 찾을 수 없습니다.");
        }
        return noticeRepository.findByProjectIdOrderByImportantDescCreatedAtDesc(projectId);
    }

    // 공지사항 상세 조회
    @GetMapping("/{projectId}/notices/{noticeId}")
    public ResponseEntity<Map<String, Object>> getNotice(@PathVariable Long projectId, @PathVariable Long noticeId) throws NotFoundException, JsonProcessingException {
        Notice notice = noticeRepository.findById(noticeId)
                .filter(n -> n.getProjectId().equals(projectId))
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        Map<String, Object> response = new HashMap<>();
        response.put("id", notice.getId());
        response.put("title", notice.getTitle());
        response.put("content", notice.getContent());
        response.put("important", notice.getImportant());
        response.put("creatorId", notice.getCreatorId());
        response.put("attachments", notice.getAttachments() != null
                ? new ObjectMapper().readValue(notice.getAttachments(), List.class)
                : new ArrayList<>());

        return ResponseEntity.ok(response);
    }



    // 공지사항 작성
    @PostMapping("/{projectId}/notices")
    public ResponseEntity<Notice> createNotice(
            @PathVariable Long projectId,
            @ModelAttribute NoticeRequest noticeRequest,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {

        if (user == null) {
            throw new UnauthorizedException("사용자 인증 실패: user 객체가 null입니다.");
        }

        String email = user.getUsername();
        User foundUser = userRepository.findByEmail(email);
        if (foundUser == null) {
            throw new UnauthorizedException("사용자를 찾을 수 없습니다.");
        }
        Long userId = foundUser.getId();

        boolean isAdmin = participantRepository.existsByProjectIdAndUserIdAndRole(
                projectId, userId, ProjectParticipant.Role.ADMIN);
        if (!isAdmin) {
            throw new UnauthorizedException("공지사항을 작성할 권한이 없습니다.");
        }

        // 공지사항 데이터 설정
        Notice notice = new Notice();
        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        notice.setImportant(noticeRequest.isImportant());
        notice.setProjectId(projectId);
        notice.setCreatorId(userId);

        // 파일 처리 및 JSON 변환
        if (noticeRequest.getAttachments() != null && !noticeRequest.getAttachments().isEmpty()) {
            try {
                List<Map<String, String>> attachmentList = saveAttachmentsAsJson(noticeRequest.getAttachments());
                ObjectMapper objectMapper = new ObjectMapper();
                String attachmentsJson = objectMapper.writeValueAsString(attachmentList);
                notice.setAttachments(attachmentsJson);
            } catch (IOException e) {
                System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
            }
        }

        Notice savedNotice = noticeRepository.save(notice);
        return new ResponseEntity<>(savedNotice, HttpStatus.CREATED);
    }


    private List<Map<String, String>> saveAttachmentsAsJson(List<MultipartFile> attachments) throws IOException {
        String uploadDir = "C:/back/uploads/";
        String baseUrl = "/downloads/"; // 다운로드 API 경로

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<Map<String, String>> attachmentList = new ArrayList<>();

        for (MultipartFile file : attachments) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(uniqueFilename);

                // 파일 저장
                Files.copy(file.getInputStream(), filePath);

                // 다운로드 URL만 저장
                Map<String, String> fileData = new HashMap<>();
                fileData.put("fileName", originalFilename); // 원본 파일 이름
                fileData.put("fileUrl", baseUrl + uniqueFilename); // 정확한 다운로드 경로
                attachmentList.add(fileData);
            }
        }

        return attachmentList; // 첨부파일 정보 리스트 반환
    }


    @GetMapping("/downloads/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String fileName) {
        String uploadDir = "C:/back/uploads/";
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

        try {
            UrlResource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다: " + fileName);
            }

            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);

        } catch (IOException ex) {
            throw new RuntimeException("파일 다운로드 실패: " + fileName, ex);
        }
    }


    // 공지사항 수정
    @PutMapping("/{projectId}/notices/{noticeId}")
    public ResponseEntity<Notice> updateNotice(
            @PathVariable Long projectId,
            @PathVariable Long noticeId,
            @RequestBody Notice updatedNotice,
            @AuthenticationPrincipal User user) throws NotFoundException {

        // 공지사항 존재 여부 확인
        Notice existingNotice = noticeRepository.findById(noticeId)
                .filter(notice -> notice.getProjectId().equals(projectId))
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        // 작성자 권한 확인
        if (!existingNotice.getCreatorId().equals(user.getId())) {
            throw new UnauthorizedException("공지사항을 수정할 권한이 없습니다.");
        }

        // 공지사항 업데이트
        existingNotice.setTitle(updatedNotice.getTitle());
        existingNotice.setContent(updatedNotice.getContent());
        existingNotice.setImportant(updatedNotice.getImportant());

        Notice savedNotice = noticeRepository.save(existingNotice);
        return new ResponseEntity<>(savedNotice, HttpStatus.OK);
    }

    // 공지사항 삭제
    @DeleteMapping("/{projectId}/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long projectId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal User user) throws NotFoundException {

        // 공지사항 존재 여부 확인
        Notice existingNotice = noticeRepository.findById(noticeId)
                .filter(notice -> notice.getProjectId().equals(projectId))
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        // 작성자 권한 확인
        if (!existingNotice.getCreatorId().equals(user.getId())) {
            throw new UnauthorizedException("공지사항을 삭제할 권한이 없습니다.");
        }

        // 공지사항 삭제
        noticeRepository.delete(existingNotice);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
