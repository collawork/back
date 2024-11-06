package com.collawork.back.service;

import com.collawork.back.dto.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private final List<MessageDTO> messages = new ArrayList<>();

    public List<MessageDTO> getMessages(String chatRoomId) {
        // 특정 채팅방의 메시지 필터링
        return messages.stream()
                .filter(msg -> msg.getChatRoomId().equals(chatRoomId))
                .toList();
    }

    public void addMessage(MessageDTO message) {
        messages.add(message);
    }
}


