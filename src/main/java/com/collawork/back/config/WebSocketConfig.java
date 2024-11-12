package com.collawork.back.config;

import com.collawork.back.handler.ChatWebSocketHandler;
import com.collawork.back.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageService chatMessageService;

    @Autowired
    public WebSocketConfig(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chattingServer/{chatRoomId}")
                .setAllowedOrigins("*");
    }

    @Bean(name = "chatWebSocketHandlerBean")
    public WebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler(chatMessageService);
    }
}