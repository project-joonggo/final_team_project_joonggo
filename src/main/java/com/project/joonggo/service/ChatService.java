package com.project.joonggo.service;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.domain.UserVO;

import java.util.List;

public interface ChatService {

    List<ChatRoomVO> getChatRoomList(long userNum);

    List<ChatCommentVO> getCommentsByRoomId(int roomId);

    // 채팅 메세지 전송.
    // 수신자 채팅방 미입장 상태일 때.
    void saveChatComment(int roomId, long userNum, String commentContent);

    // 수신자 채팅방 입장 상태일 때.
    void saveChatCommentEnterUser(int roomId, long userNum, String commentContent);

    boolean isUserInRoom(int roomId, long userNum);

    void addUserToRoom(int roomId, long userNum);

    ChatRoomVO createChatRoom(ChatRoomVO chatRoomVO);

    void joinChatRoom(ChatJoinVO chatJoinVO);

    ChatRoomVO findExistingRoom(int sellerId, String roomName);

    // 특정 채팅방의 읽지 않은 메시지 수 조회
    int getUnreadCount(int roomId, long userNum);

    // 전체 읽지 않은 메시지 수 조회
    int getTotalUnreadCount(long userNum);

    // 채팅방 메시지 읽음 처리
    void markAsRead(int roomId, long userNum);

    int getReceiverUserNum(int roomId, long userNum);

    Long otherUser(int roomId, int userNum);

    void leaveRoom(int roomId, int userNum);

    int getRoomUserCount(int roomId);

    UserVO getUserInfo(int receiverNum);
}
