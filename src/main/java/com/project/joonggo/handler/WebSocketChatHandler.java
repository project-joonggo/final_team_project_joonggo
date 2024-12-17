    package com.project.joonggo.handler;


    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Component;
    import org.springframework.web.socket.TextMessage;
    import org.springframework.web.socket.WebSocketSession;
    import org.springframework.web.socket.handler.TextWebSocketHandler;

    @Slf4j(topic="WebSocketChatHandler")
    @Component
    public class WebSocketChatHandler extends TextWebSocketHandler {

        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            log.info("payload > {}", payload);
            TextMessage textMessage = new TextMessage("");
        }
    }
