package com.project.joonggo.controller;


import com.project.joonggo.domain.*;
import com.project.joonggo.security.AuthUser;
import com.project.joonggo.service.ChatService;
import com.project.joonggo.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final LoginService loginService;

    // 채팅방 목록을 가져오기
    @GetMapping("/chatRoomList")
    public String getChatRoomList(Model m) {
        // SecurityContext에서 직접 userNum 가져오기
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userNum = authUser.getUserVO().getUserNum();

        List<ChatRoomVO> chatRoomList = chatService.getChatRoomList(userNum);

        // 각 채팅방의 안 읽은 메시지 수
        Map<Integer, Integer> unreadCounts = new HashMap<>();
        // 각 채팅방의 수신자 정보
        Map<Integer, Long> receivers = new HashMap<>();

        for (ChatRoomVO room : chatRoomList) {
            unreadCounts.put(room.getRoomId(), chatService.getUnreadCount(room.getRoomId(), userNum));
            // 채팅방의 상대방(수신자) 정보 저장
            receivers.put(room.getRoomId(), chatService.otherUser(room.getRoomId(), (int) userNum));
        }

        m.addAttribute("chatRoomList", chatRoomList);
        m.addAttribute("unreadCounts", unreadCounts);
        m.addAttribute("receivers", receivers);
        m.addAttribute("totalUnreadCount", chatService.getTotalUnreadCount(userNum));
        m.addAttribute("userNum", userNum);

        return "chat/chatRoomList";
    }

    // 채팅방 페이지에 입장하기
    @GetMapping("/enterRoom")
    @ResponseBody
    public ChatRoomDTO enterChatRoom(@RequestParam("roomId") int roomId,
                                     @RequestParam("userNum") int userNum) {
        try {
            // 사용자가 해당 채팅방에 이미 참여했는지 확인
            if (!chatService.isUserInRoom(roomId, userNum)) {
                // 채팅방에 사용자 추가 (입장)
                chatService.addUserToRoom(roomId, userNum);
            }

            ChatRoomDTO response = new ChatRoomDTO();
            response.setComments(chatService.getCommentsByRoomId(roomId));

            long otherUserNum = chatService.otherUser(roomId, userNum);
            response.setOtherUser(loginService.getUserById(otherUserNum));

            return response;
        } catch (Exception e) {
            log.error("Error entering chat room: ", e);
            throw e;
        }
    }

    @PostMapping("/room/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Map<String, Object> request) {
        log.info("Create chat room request: {}", request);
        try {
            // 요청에서 필요한 데이터 추출
            int userNum = Integer.parseInt(request.get("userNum").toString());
            int sellerId = Integer.parseInt(request.get("sellerId").toString());
            String roomName = request.get("roomName").toString();

            // 기존 채팅방 확인
            ChatRoomVO existingRoom = chatService.findExistingRoom(sellerId, roomName);
            ChatRoomVO chatRoom;

            if (existingRoom != null) {
                log.info("Found existing room: {}", existingRoom);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("isExisting", true);
                response.put("redirectUrl", "/chat/chatRoomList?userNum=" + userNum);
                return ResponseEntity.ok(response);
            }

            // 새 채팅방 생성
            ChatRoomVO chatRoomVO = new ChatRoomVO();
            chatRoomVO.setUserNum(sellerId);  // 판매자의 ID로 설정
            chatRoomVO.setRoomName(roomName);
            chatRoom = chatService.createChatRoom(chatRoomVO);

            // 구매자(userNum) 참여
            ChatJoinVO buyerJoin = new ChatJoinVO();
            buyerJoin.setRoomId(chatRoom.getRoomId());
            buyerJoin.setUserNum(userNum);
            chatService.joinChatRoom(buyerJoin);

            // 판매자(sellerId) 참여
            ChatJoinVO sellerJoin = new ChatJoinVO();
            sellerJoin.setRoomId(chatRoom.getRoomId());
            sellerJoin.setUserNum(sellerId);
            chatService.joinChatRoom(sellerJoin);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("isExisting", false);  // 기존 채팅방 여부
            response.put("redirectUrl", "/chat/chatRoomList?userNum=" + userNum);

            log.info("Response data: {}", response);  // 응답 데이터 로깅
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating chat room: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/read/{roomId}")
    @ResponseBody
    public ResponseEntity<Void> markAsRead( @PathVariable(name = "roomId") int roomId,
                                            @RequestParam(name = "userNum") int userNum) {
        chatService.markAsRead(roomId, userNum);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/total")
    @ResponseBody
    public ResponseEntity<Integer> getTotalUnreadCount(@RequestParam("userNum") int userNum) {
        log.info("Getting total unread count for userNum: {}", userNum);
        int count = chatService.getTotalUnreadCount(userNum);
        log.info("Total unread count: {}", count);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/room/leave")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> leaveRoom(@RequestParam int roomId, @RequestParam int userNum) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatService.leaveRoom(roomId, userNum);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/room/userCount/{roomId}")
    @ResponseBody
    public ResponseEntity<Integer> getRoomUserCount(@PathVariable int roomId) {
        int count = chatService.getRoomUserCount(roomId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/unread/rooms")
    @ResponseBody
    public ResponseEntity<Map<Integer, Integer>> getRoomUnreadCounts(@RequestParam("userNum") int userNum) {
        log.info("Getting unread counts for all rooms for userNum: {}", userNum);
        Map<Integer, Integer> unreadCounts = new HashMap<>();

        // 사용자의 모든 채팅방 조회
        List<ChatRoomVO> chatRoomList = chatService.getChatRoomList(userNum);

        // 각 채팅방의 안 읽은 메시지 수 계산
        for (ChatRoomVO room : chatRoomList) {
            unreadCounts.put(room.getRoomId(), chatService.getUnreadCount(room.getRoomId(), userNum));
        }

        log.info("Room unread counts: {}", unreadCounts);
        return ResponseEntity.ok(unreadCounts);
    }
}
