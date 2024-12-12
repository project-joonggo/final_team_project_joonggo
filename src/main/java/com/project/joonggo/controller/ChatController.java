package com.project.joonggo.controller;


import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 채팅방 목록을 가져오기
    @GetMapping("/roomList")
    public String getChatRoomList(Model m, @RequestParam int userNum) {
        List<ChatRoomVO> chatRoomList = chatService.getChatRoomList(userNum);
        m.addAttribute("chatRoomList", chatRoomList);
        return "chatRoomList";
    }

    // 채팅방에 입장하기
    @GetMapping("/enterRoom")
    public String enterChatRoom(@RequestParam int roomId, @RequestParam int userNum, Model m) {
        // 사용자가 해당 채팅방에 이미 참여했는지 확인
        if (!chatService.isUserInRoom(roomId, userNum)) {
            // 채팅방에 사용자 추가 (입장)
            chatService.addUserToRoom(roomId, userNum);
        }

        // 채팅방에 대한 메시지 목록을 가져오기
        List<ChatCommentVO> commentList = chatService.getCommentsByRoomId(roomId);
        m.addAttribute("roomId", roomId);
        m.addAttribute("commentList", commentList);
        return "chatRoom";  // 채팅방 페이지
    }

    // 메시지 전송 (POST)
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam int roomId, @RequestParam int userNum, @RequestParam String messageContent) {
        chatService.saveChatComment(roomId, userNum, messageContent);
        return "redirect:/chat/enterRoom?roomId=" + roomId;
    }
}
