package com.collawork.back.handler;

import com.collawork.back.dto.MessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    //세션 관리 set
    private static Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());
    //json으로 넘어오는 데이터를 매핑시키기위한 객체
    private static ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("메세지 전송 : "+session.getId() + " : "+message.getPayload());
        String payload = message.getPayload();
        //키벨류로 넘어오는 getPayload를 dto와 매핑

        MessageDTO msg = objectMapper.readValue(payload, MessageDTO.class);

        //이 msg dto에 담겨있다

        synchronized (clients){
            for (WebSocketSession client : clients){
                if (!client.equals(session)){
                    if("join".equals(msg.getType())){
                        client.sendMessage(new TextMessage(msg.getSenderId()+"님이 입장하셨습니다. 모두 반겨 주세요 ~~"));
                    } else if ("leave".equals(msg.getType())){
                        client.sendMessage(new TextMessage(msg.getSenderId()+"님이 퇴장하셨습니다. "));
                    } else if ("message".equals(msg.getType())) {
                        client.sendMessage(new TextMessage(msg.getSenderId()+" : "+msg.getContent()));
                    }
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //통신중 특정 클라이언트 에러가 발생할때 메소드
        System.out.println("에러발생 : " +session.getId());
        //에러 내역
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session);
        System.out.println("대화방 종료 : "+session.getId());
    }
}
