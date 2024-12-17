package com.project.joonggo.controller;


import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 채팅방 목록을 가져오기
    @GetMapping("/chatRoomList")
    public String getChatRoomList(Model m, @RequestParam("userNum") int userNum) {
        log.info("userNum > {}", userNum);
        List<ChatRoomVO> chatRoomList = chatService.getChatRoomList(userNum);
        m.addAttribute("chatRoomList", chatRoomList);
        m.addAttribute("userNum", userNum);
        log.info("chatRoomList > {}", chatRoomList);
        log.info("Model > {}", m);

        return "chat/chatRoomList";
    }

    // 채팅방에 입장하기
    @GetMapping("/enterRoom")
    @ResponseBody
    public List<ChatCommentVO> enterChatRoom(@RequestParam("roomId") int roomId,
                                @RequestParam("userNum") int userNum, Model m) {
        // 사용자가 해당 채팅방에 이미 참여했는지 확인
        if (!chatService.isUserInRoom(roomId, userNum)) {
            // 채팅방에 사용자 추가 (입장)
            chatService.addUserToRoom(roomId, userNum);
        }

        return chatService.getCommentsByRoomId(roomId);
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

            // 채팅방 생성
            ChatRoomVO chatRoomVO = new ChatRoomVO();
            chatRoomVO.setUserNum(userNum);
            chatRoomVO.setRoomName(roomName);

            ChatRoomVO createdRoom = chatService.createChatRoom(chatRoomVO);

            // 구매자(userNum) 참여
            ChatJoinVO buyerJoin = new ChatJoinVO();
            buyerJoin.setRoomId(createdRoom.getRoomId());
            buyerJoin.setUserNum(userNum);
            chatService.joinChatRoom(buyerJoin);

            // 판매자(sellerId) 참여
            ChatJoinVO sellerJoin = new ChatJoinVO();
            sellerJoin.setRoomId(createdRoom.getRoomId());
            sellerJoin.setUserNum(sellerId);
            chatService.joinChatRoom(sellerJoin);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
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

}
