package com.project.joonggo.controller;


import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
//        return "/chat/chatRoomList :: #roomList";
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

//        // 채팅방에 대한 메시지 목록을 가져오기
//        List<ChatCommentVO> commentList = chatService.getCommentsByRoomId(roomId);
//        m.addAttribute("roomId", roomId);
//        m.addAttribute("commentList", commentList);
//        return "/chat/chatRoom";  // 채팅방 페이지
        return chatService.getCommentsByRoomId(roomId);
    }


}
