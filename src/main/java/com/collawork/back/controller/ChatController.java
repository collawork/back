package com.collawork.back.controller;

import com.collawork.back.model.Message;
import com.collawork.back.repository.MessageRepository;
import com.collawork.back.repository.MessageType;
import com.collawork.back.service.ChatMessageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChatMessageService chatMessageService;

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + "/uploads/";



    @PostConstruct
    public void init() {
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    //해당 채팅방의 모든 메세지 반환하는 메서드
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable ("chatRoomId") Long chatRoomId) {
        List<Message> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId);

        return ResponseEntity.ok(messages);
    }

    //채팅방 이름 가져오는 메서드
    @GetMapping("/roomName/{chatRoomId}")
    public ResponseEntity<String> getRoomName(@PathVariable("chatRoomId") Long chatRoomId) {
        String roomName = chatMessageService.getChatRoomName(chatRoomId);

        return ResponseEntity.ok(roomName);
    }

    //파일업로드 메서드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") Long senderId,
            @RequestParam("chatRoomId") Long chatRoomId,
            @RequestParam("timestamp") String time ) throws ParseException {

                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm:ss");
                Date date = sdf.parse(time);
                Timestamp timestamp = new Timestamp(date.getTime());

        try {

            Path filePath = Paths.get(FILE_DIRECTORY, file.getOriginalFilename());
            Files.write(filePath, file.getBytes());


            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(file.getOriginalFilename())
                    .toUriString();


            Message message = new Message();
            message.setSenderId(senderId);
            message.setChatRoomId(chatRoomId);
            message.setMessageType(MessageType.file);
            message.setFileUrl(fileUrl);
            message.setCreatedAt(timestamp);

            messageRepository.save(message);

            return ResponseEntity.ok().body(Map.of("fileUrl", fileUrl));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 업로드 오류: " + e.getMessage());
        }
    }
}