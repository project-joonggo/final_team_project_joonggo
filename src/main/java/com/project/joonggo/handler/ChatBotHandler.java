package com.project.joonggo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j(topic = "WebSocketChatHandler")
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("채팅 log : payload > {}", payload);
        TextMessage textMessage = new TextMessage("입장했습니다.");
        session.sendMessage(textMessage);
    }
}
