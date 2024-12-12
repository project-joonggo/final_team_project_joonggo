package com.project.joonggo.config;

import com.project.joonggo.handler.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.socket.WebSocketHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final WebSocketHandler webSocketHandler;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketConfig(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 설정
        registry.enableSimpleBroker("/topic"); // /topic으로 시작하는 메시지를 구독할 수 있도록 설정
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트에서 /app으로 시작하는 경로로 요청이 올 경우 이를 처리
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 핸들러 등록
        registry.addEndpoint("/ws/chat")
                .withSockJS();
//                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler(messagingTemplate); // WebSocketHandler 객체를 반환하며, 메시징 템플릿을 주입
    }
}