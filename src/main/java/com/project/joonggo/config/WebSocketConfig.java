package com.project.joonggo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
//                .setAllowedOrigins("*")     // 필요할 경우에만 설정. 일단 추가. 나중에 주석처리합시다.
                .withSockJS();  // WebSocket 엔드포인트 등록


        registry.addEndpoint("/notifications").withSockJS();  // 알림용 WebSocket 엔드포인트
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic", "/user");  // 큐와 토픽 경로 처리
        registry.setApplicationDestinationPrefixes("/app");     // prefix 설정
    }

    /* 챗봇 전용 끝 */

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // 로그인과 로그아웃에 관계없이 WebSocket 연결을 계속 유지
                return message;
            }
        });
    }
}
