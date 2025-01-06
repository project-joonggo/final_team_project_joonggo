package com.project.joonggo.controller;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.UserVO;
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

import java.util.HashMap;
import java.util.HashSet;
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
    private static final Map<Integer, Set<Integer>> activeUsers = new ConcurrentHashMap<>();

    // 사용자가 채팅방에 입장할 때
    @MessageMapping("/chat/{roomId}/enter")
    public void handleEnter(@DestinationVariable("roomId") int roomId, ChatJoinVO chatJoinVO) {
        int userNum = (int) chatJoinVO.getUserNum();
        log.info("User {} entered chat room {}", chatJoinVO.getUserNum(), roomId);
        log.info("Before modification - Complete activeUsers map: {}", activeUsers);

        // 모든 채팅방에서 현재 사용자 제거
        Set<Integer> roomsToRemove = new HashSet<>();
        activeUsers.forEach((existingRoomId, users) -> {
            users.remove(userNum);
            if (users.isEmpty()) {
                roomsToRemove.add(existingRoomId);
            }
        });

        // 빈 채팅방 제거
        roomsToRemove.forEach(activeUsers::remove);

        // 새로운 채팅방에 사용자 추가
        activeUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(userNum);

        log.info("After modification - Complete activeUsers map: {}", activeUsers);
        log.info("Current active users in room {}: {}", roomId, activeUsers.get(roomId));

        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/enter", chatJoinVO);
    }

    // 사용자가 채팅방을 나갈 때
    @MessageMapping("/chat/{roomId}/leave")
    public void handleLeave(@DestinationVariable("roomId") int roomId, ChatJoinVO joinInfo) {
        int userNum = (int) joinInfo.getUserNum();
        log.info("User {} left chat room {}", joinInfo.getUserNum(), roomId);

        // 해당 사용자를 모든 채팅방에서 제거
        Set<Integer> roomsToRemove = new HashSet<>();
        activeUsers.forEach((existingRoomId, users) -> {
            users.remove(userNum);
            if (users.isEmpty()) {
                roomsToRemove.add(existingRoomId);
            }
        });

        // 빈 채팅방 제거
        roomsToRemove.forEach(activeUsers::remove);

        log.info("Complete activeUsers map after leave: {}", activeUsers);

        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/leave", joinInfo);
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatCommentVO chatCommentVO) {
        log.info("Current active users map: {}", activeUsers);
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
            log.info("Active users in room {}: {}", chatCommentVO.getRoomId(), roomUsers);

            // 메시지 수신자 정보 조회
            int receiverNum = chatService.getReceiverUserNum(
                    chatCommentVO.getRoomId(),
                    chatCommentVO.getCommentUserNum());

            log.info("Receiver user number: {}", receiverNum);

            boolean isReceiverActive = roomUsers != null && roomUsers.contains(receiverNum);

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
            // 수신자의 전체 읽지 않은 메시지 수 조회
            int totalUnreadCount = chatService.getTotalUnreadCount(receiverNum);
            // 해당 채팅방의 읽지 않은 메시지 수 조회
            int roomUnreadCount = chatService.getUnreadCount(chatCommentVO.getRoomId(), receiverNum);

            // 수신자의 정보를 조회하여 메시지에 포함
            UserVO receiverInfo = chatService.getUserInfo(receiverNum);
            chatCommentVO.setOtherUser(receiverInfo);

            // 채팅방에 메시지와 읽지 않은 메시지 수를 함께 전송
            Map<String, Object> response = new HashMap<>();
            response.put("message", chatCommentVO);
            response.put("roomUnreadCount", roomUnreadCount);    // 채팅방별 카운트
            response.put("totalUnreadCount", totalUnreadCount);  // 전체 카운트
//            response.put("receiverNum", receiverNum);

            // 채팅방에 메세지 전송
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatCommentVO.getRoomId(),
                    response
            );
            // 수신자에게 전체 읽지 않은 메세지 수 전송
            messagingTemplate.convertAndSend(
                    "/topic/user/" + receiverNum + "/unread",  // 사용자별 구독 토픽
                    totalUnreadCount
            );

            log.info("Sending message to room {}: {}", chatCommentVO.getRoomId(), response);
            log.info("roomUnreadCount: {}", roomUnreadCount);

        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }
}
