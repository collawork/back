package com.collawork.back.config;

import com.collawork.back.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public ChatWebSocketHandler chatWebsocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebsocketHandler(), "/chattingServer/*")
                .setAllowedOrigins("*");
    }
}
