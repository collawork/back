package com.collawork.back.controller;

import com.collawork.back.model.Message;
import com.collawork.back.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable ("chatRoomId") Long chatRoomId) {
        List<Message> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}