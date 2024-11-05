package com.collawork.back.service;

import com.collawork.back.dto.MessageDTO;
import com.collawork.back.model.Message;
import com.collawork.back.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setChatRoomId(messageDTO.getChatRoomId());
        message.setContent(messageDTO.getContent());
        message.setMessageType(messageDTO.getMessageType());
        message.setFileUrl(messageDTO.getFileUrl());
        return messageRepository.save(message); // 메시지 DB에 저장
    }

    public List<Message> getMessagesByChatRoomId(String chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId); // 특정 채팅방의 메시지 조회
    }
}