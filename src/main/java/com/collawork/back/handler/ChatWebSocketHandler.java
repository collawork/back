package com.collawork.back.handler;

import com.collawork.back.dto.MessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    // chatRoomId 별로 WebSocketSession들을 저장하기 위한 Map
    private static Map<String, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    // JSON 데이터를 매핑하기 위한 ObjectMapper
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // URI에서 chatRoomId 추출
        String path = session.getUri().getPath();
        String chatRoomId = path.split("/")[2]; // e.g., /chattingServer/1 에서 '1' 추출

        // 채팅방에 세션 추가
        chatRooms.computeIfAbsent(chatRoomId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
        System.out.println("세션 추가: " + session.getId() + ", 채팅방: " + chatRoomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 세션의 URI에서 채팅방 ID 추출
        String path = session.getUri().getPath();
        String chatRoomId = path.split("/")[2];

        System.out.println("메세지 전송 : " + session.getId() + " : " + message.getPayload());
        String payload = message.getPayload();

        // JSON payload를 MessageDTO 객체에 매핑
        MessageDTO msg = objectMapper.readValue(payload, MessageDTO.class);

        // 해당 chatRoomId에 있는 클라이언트에게만 메시지 전송
        synchronized (chatRooms.get(chatRoomId)) {
            for (WebSocketSession client : chatRooms.get(chatRoomId)) {
                if (!client.equals(session)) {
                    if ("join".equals(msg.getType())) {
                        client.sendMessage(new TextMessage(msg.getSenderId() + "님이 입장하셨습니다. 모두 반겨 주세요 ~~"));
                    } else if ("leave".equals(msg.getType())) {
                        client.sendMessage(new TextMessage(msg.getSenderId() + "님이 퇴장하셨습니다."));
                    } else if ("message".equals(msg.getType())) {
                        client.sendMessage(new TextMessage(msg.getSenderId() + " : " + msg.getContent()));
                    }
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("에러발생 : " + session.getId());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션의 URI에서 채팅방 ID 추출
        String path = session.getUri().getPath();
        String chatRoomId = path.split("/")[2];

        // 세션을 채팅방에서 제거
        Set<WebSocketSession> clientsInRoom = chatRooms.get(chatRoomId);
        if (clientsInRoom != null) {
            clientsInRoom.remove(session);
            if (clientsInRoom.isEmpty()) {
                chatRooms.remove(chatRoomId); // 채팅방에 남은 사용자가 없으면 방 제거
            }
        }
        System.out.println("대화방 종료 : " + session.getId());
    }
}