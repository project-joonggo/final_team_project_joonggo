package com.project.joonggo.config;

import com.project.joonggo.handler.CustomWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketConfig(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 핸들러 등록
        registry.addHandler(webSocketHandler(), "/ws/chat")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new CustomWebSocketHandler(messagingTemplate); // WebSocketHandler 객체를 반환하며, 메시징 템플릿을 주입
    }
}

    /* 챗봇 전용 시작 : implements WebSocketMessageBrokerConfigurer */

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws/chatbot").withSockJS();       // 웹 소켓을 사용하기 위해 설정하는 부분
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/app");     // prefix 설정
//        registry.enableSimpleBroker("/topic");                  // topic이라는 주제에 브로커를 설정
//    }

    /* 챗봇 전용 끝 */
