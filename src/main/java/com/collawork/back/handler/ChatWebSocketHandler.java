package com.collawork.back.handler;

import com.collawork.back.dto.MessageDTO;
import com.collawork.back.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private Map<String, Set<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private final ChatMessageService chatMessageService;


    @Autowired
    public ChatWebSocketHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chatRoomId = getChatRoomId(session);
        chatRoomSessions.putIfAbsent(chatRoomId, ConcurrentHashMap.newKeySet());
        chatRoomSessions.get(chatRoomId).add(session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String chatRoomId = getChatRoomId(session);
        MessageDTO msg = objectMapper.readValue(message.getPayload(), MessageDTO.class);


        chatMessageService.saveMessage(msg);



    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String chatRoomId = getChatRoomId(session);
        chatRoomSessions.get(chatRoomId).remove(session);

    }

    private String getChatRoomId(WebSocketSession session) {
        return session.getUri().getPath().split("/")[2];
    }
}