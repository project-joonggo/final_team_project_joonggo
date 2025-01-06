package com.project.joonggo.service;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.domain.UserVO;
import com.project.joonggo.repository.ChatCommentMapper;
import com.project.joonggo.repository.ChatJoinMapper;
import com.project.joonggo.repository.ChatRoomMapper;
import com.project.joonggo.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatJoinMapper chatJoinMapper;
    private final ChatCommentMapper chatCommentMapper;
    private final UserMapper userMapper;

    @Override
    public List<ChatRoomVO> getChatRoomList(long userNum) {
        return chatRoomMapper.getChatRoomList(userNum);
    }

    @Override
    public List<ChatCommentVO> getCommentsByRoomId(int roomId) {
        return chatCommentMapper.getCommentsByRoomId(roomId);
    }

    @Override
    public void saveChatComment(int roomId, long userNum, String commentContent) {
        ChatCommentVO chatCommentVO = new ChatCommentVO();
        chatCommentVO.setRoomId(roomId);
        chatCommentVO.setCommentUserNum(userNum);
        chatCommentVO.setCommentContent(commentContent);

        chatCommentMapper.saveChatComment(chatCommentVO);
    }

    @Override
    public void saveChatCommentEnterUser(int roomId, long userNum, String commentContent) {
        ChatCommentVO chatCommentVO = new ChatCommentVO();
        chatCommentVO.setRoomId(roomId);
        chatCommentVO.setCommentUserNum(userNum);
        chatCommentVO.setCommentContent(commentContent);

        chatCommentMapper.saveChatCommentEnterUser(chatCommentVO);
    }

    @Override
    public void addUserToRoom(int roomId, long userNum) {
        ChatJoinVO chatJoinVO = new ChatJoinVO();
        chatJoinVO.setRoomId(roomId);
        chatJoinVO.setUserNum(userNum);
        chatJoinMapper.addUserToRoom(chatJoinVO);
    }

    @Override
    public ChatRoomVO createChatRoom(ChatRoomVO chatRoomVO) {
        chatRoomMapper.createChatRoom(chatRoomVO);
        return chatRoomVO;
    }

    @Override
    public void joinChatRoom(ChatJoinVO chatJoinVO) {
        chatJoinMapper.joinChatRoom(chatJoinVO);
    }

    @Override
    public ChatRoomVO findExistingRoom(int sellerId, String roomName) {
        return chatRoomMapper.findExistingRoom(sellerId, roomName);
    }

    @Override
    public boolean isUserInRoom(int roomId, long userNum) {
        return chatJoinMapper.isUserInRoom(roomId, userNum);
    }

    // 특정 채팅방의 읽지 않은 메시지 수 조회
    @Override
    public int getUnreadCount(int roomId, long userNum) {
        return chatCommentMapper.countUnreadMessages(roomId, userNum);
    }

    // 전체 읽지 않은 메시지 수 조회
    @Override
    public int getTotalUnreadCount(long userNum) {
        return chatCommentMapper.countTotalUnreadMessages(userNum);
    }

    // 채팅방 메시지 읽음 처리
    @Override
    public void markAsRead(int roomId, long userNum) {
        chatCommentMapper.updateReadStatus(roomId, userNum);
    }

    @Override
    public int getReceiverUserNum(int roomId, long userNum) {
        return chatJoinMapper.getReceiverUserNum(roomId, userNum);
    }

    // 채팅방 본인 외 다른 사용자
    @Override
    public long otherUser(int roomId, int userNum) {
        return chatJoinMapper.otherUser(roomId, userNum);
    }

    @Override
    public void leaveRoom(int roomId, int userNum) {
        chatJoinMapper.deleteUserFromRoom(roomId, userNum);
    }

    @Override
    public int getRoomUserCount(int roomId) {
        return chatJoinMapper.countRoomUsers(roomId);
    }

    @Override
    public UserVO getUserInfo(int receiverNum) {
        return userMapper.getUserInfo(receiverNum);
    }

}
