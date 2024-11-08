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
        System.out.println("웹소켓 연결 : " + session.getId() + " 채팅방 : " + chatRoomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String chatRoomId = getChatRoomId(session);
        MessageDTO msg = objectMapper.readValue(message.getPayload(), MessageDTO.class);

        // 메시지 저장
        chatMessageService.saveMessage(msg);


        Map<String, String> response = new HashMap<>();
        response.put("senderId", msg.getSenderId());
        response.put("message", msg.getMessage());
        response.put("type", msg.getType());

        // 클라이언트에게 JSON 형식으로 전송
        String responseJson = objectMapper.writeValueAsString(response);

        synchronized (chatRoomSessions) {
            for (WebSocketSession client : chatRoomSessions.get(chatRoomId)) {
                if (!client.equals(session)) {
                    if ("join".equals(msg.getType())) {
                        Map<String, String> joinResponse = new HashMap<>();
                        joinResponse.put("message", msg.getSenderId() + "님이 입장하셨습니다.");
                        client.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinResponse)));
                    } else if ("leave".equals(msg.getType())) {
                        Map<String, String> leaveResponse = new HashMap<>();
                        leaveResponse.put("message", msg.getSenderId() + "님이 퇴장하셨습니다.");
                        client.sendMessage(new TextMessage(objectMapper.writeValueAsString(leaveResponse)));
                    } else if ("message".equals(msg.getType())) {
                        client.sendMessage(new TextMessage(responseJson)); // 일반 메시지
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String chatRoomId = getChatRoomId(session);
        chatRoomSessions.get(chatRoomId).remove(session);
        System.out.println("대화방 종료 : " + session.getId() + " 채팅방 : " + chatRoomId);
    }

    private String getChatRoomId(WebSocketSession session) {
        return session.getUri().getPath().split("/")[2];
    }
}