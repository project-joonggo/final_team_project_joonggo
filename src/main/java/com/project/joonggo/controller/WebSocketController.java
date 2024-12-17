package com.project.joonggo.controller;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatCommentVO chatCommentVO) {
        log.info("Received message Payload : {}", chatCommentVO);

        if (chatCommentVO == null
                || chatCommentVO.getRoomId() == 0
                || chatCommentVO.getCommentUserNum() == 0
                || chatCommentVO.getCommentContent() == null) {
            log.error("Invalid message data received: {}", chatCommentVO);
            return;
        }

        log.info("Received message: {}", chatCommentVO);

        try {
            chatService.saveChatComment(
                    chatCommentVO.getRoomId(),
                    chatCommentVO.getCommentUserNum(),
                    chatCommentVO.getCommentContent()
            );

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatCommentVO.getRoomId(),
                    chatCommentVO
            );
        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }
}
