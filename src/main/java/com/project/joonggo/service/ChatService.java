package com.project.joonggo.service;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.ChatRoomVO;

import java.util.List;

public interface ChatService {

    List<ChatRoomVO> getChatRoomList(int userNum);

    List<ChatCommentVO> getCommentsByRoomId(int roomId);

    // 채팅 메세지 전송.
    // 수신자 채팅방 미입장 상태일 때.
    void saveChatComment(int roomId, int userNum, String commentContent);

    // 수신자 채팅방 입장 상태일 때.
    void saveChatCommentEnterUser(int roomId, int userNum, String commentContent);

    boolean isUserInRoom(int roomId, int userNum);

    void addUserToRoom(int roomId, int userNum);

    ChatRoomVO createChatRoom(ChatRoomVO chatRoomVO);

    void joinChatRoom(ChatJoinVO chatJoinVO);

    ChatRoomVO findExistingRoom(int sellerId, String roomName);

    // 특정 채팅방의 읽지 않은 메시지 수 조회
    int getUnreadCount(int roomId, int userNum);

    // 전체 읽지 않은 메시지 수 조회
    int getTotalUnreadCount(int userNum);

    // 채팅방 메시지 읽음 처리
    void markAsRead(int roomId, int userNum);

    int getReceiverUserNum(int roomId, int userNum);

}
