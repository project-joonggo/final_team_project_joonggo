package com.project.joonggo.controller;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatCommentVO chatCommentVO) {
        chatService.saveChatComment(
                chatCommentVO.getRoomId(),
                chatCommentVO.getCommentUserNum(),
                chatCommentVO.getCommentContent()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatCommentVO.getRoomId(),
                chatCommentVO
        );
    }
}
