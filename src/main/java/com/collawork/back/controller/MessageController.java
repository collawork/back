package com.collawork.back.controller;


import com.collawork.back.dto.MessageDTO;
import com.collawork.back.model.Message;
import com.collawork.back.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{chatRoomId}")
    public List<Message> getMessages(@PathVariable String chatRoomId) {
        return messageService.getMessagesByChatRoomId(chatRoomId);
    }

    @PostMapping
    public Message createMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.saveMessage(messageDTO); // DTO를 통해 메시지 저장
    }
}