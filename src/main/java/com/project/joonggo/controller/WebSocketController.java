package com.project.joonggo.controller;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방별 접속 중인 사용자를 추적하기 위한 Map
    private static Map<Integer, Set<Integer>> activeUsers = new ConcurrentHashMap<>();

    // 사용자가 채팅방에 입장할 때
    @MessageMapping("/chat/{roomId}/enter")
    public void handleEnter(@DestinationVariable("roomId") int roomId, ChatJoinVO joinInfo) {
        log.info("User {} entered chat room {}", joinInfo.getUserNum(), roomId);

        // 채팅방의 활성 사용자 목록에 추가
        activeUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(joinInfo.getUserNum());

        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/enter", joinInfo);
    }

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

        try {
            // 현재 채팅방의 활성 사용자 확인
            Set<Integer> roomUsers = activeUsers.get(chatCommentVO.getRoomId());

            // 메시지 수신자가 채팅방에 접속해있는지 확인
            int receiverNum = chatService.getReceiverUserNum(
                    chatCommentVO.getRoomId(),
                    chatCommentVO.getCommentUserNum());

            log.info("roomUsers > {}", roomUsers);
            log.info("Receiver Num: {}", receiverNum);

            boolean isReceiverActive = roomUsers != null && roomUsers.contains(receiverNum);

            log.info("Is Receiver Active: {}", isReceiverActive);

            // 수신자의 접속 상태에 따라 다른 메서드 호출
            if (isReceiverActive) {
                chatService.saveChatCommentEnterUser(
                        chatCommentVO.getRoomId(),
                        chatCommentVO.getCommentUserNum(),
                        chatCommentVO.getCommentContent()
                );
            } else {
                chatService.saveChatComment(
                        chatCommentVO.getRoomId(),
                        chatCommentVO.getCommentUserNum(),
                        chatCommentVO.getCommentContent()
                );
            }

            log.info("Final ChatCommentVO before save: {}", chatCommentVO);

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatCommentVO.getRoomId(),
                    chatCommentVO
            );
        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }
}
