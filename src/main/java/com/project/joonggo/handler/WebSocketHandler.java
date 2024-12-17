package com.project.joonggo.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends AbstractWebSocketHandler {
    // extends TextWebSocketHandler
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        // WebSocket을 통해 실시간으로 해당 채팅방에 있는 모든 클라이언트에게 메세지 전송.
        messagingTemplate.convertAndSend("/topic/chat/", payload);
    }


    //    websocket test : chrome 프로그램으로 테스트 완료. 잠깐 주석.
//    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        CLIENTS.put(session.getId(), session);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        CLIENTS.remove(session.getId());
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String id = session.getId();                        // 메세지를 보낸 아이디.
//        CLIENTS.entrySet().forEach(arg -> {
//            if (!arg.getKey().equals(id)) {                   // 같은 아이디가 아니면 메세지 전달.
//                try {
//                    arg.getValue().sendMessage(message);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
