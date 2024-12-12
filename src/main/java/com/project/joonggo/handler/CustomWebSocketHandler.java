package com.project.joonggo.handler;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class CustomWebSocketHandler extends AbstractWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CustomWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        messagingTemplate.convertAndSend("/topic/messages", payload);
    }
}
