package com.collawork.back.controller;

import com.collawork.back.model.ChatRoom;
import com.collawork.back.repository.ChatRoomRepository;
import com.collawork.back.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoom> getChatRoomInfo(@PathVariable Long chatRoomId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);

        if (chatRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(chatRoom.get());
    }
}
