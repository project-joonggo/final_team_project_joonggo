package com.project.joonggo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {

    // /app/notifications로 들어오는 메시지 처리
    @MessageMapping("/notifications")
    @SendTo("/queue/notifications")  // /topic/notifications 구독자에게 메시지 전송
    public String handleNotification(String message) throws Exception {
        log.info("알림 메시지 > {}", message);
        return "알림 메시지 응답: " + message;  // 응답을 반환하여 /topic/notifications에 전송
    }
}