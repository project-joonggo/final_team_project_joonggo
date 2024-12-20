package com.project.joonggo.service;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatJoinVO;
import com.project.joonggo.domain.ChatRoomVO;
import com.project.joonggo.repository.ChatCommentMapper;
import com.project.joonggo.repository.ChatJoinMapper;
import com.project.joonggo.repository.ChatRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatJoinMapper chatJoinMapper;
    private final ChatCommentMapper chatCommentMapper;

    @Override
    public List<ChatRoomVO> getChatRoomList(int userNum) {
        return chatRoomMapper.getChatRoomList(userNum);
    }

    @Override
    public List<ChatCommentVO> getCommentsByRoomId(int roomId) {
        return chatCommentMapper.getCommentsByRoomId(roomId);
    }

    @Override
    public void saveChatComment(int roomId, int userNum, String commentContent) {
        ChatCommentVO chatCommentVO = new ChatCommentVO();
        chatCommentVO.setRoomId(roomId);
        chatCommentVO.setCommentUserNum(userNum);
        chatCommentVO.setCommentContent(commentContent);

        chatCommentMapper.saveChatComment(chatCommentVO);
    }

    @Override
    public void saveChatCommentEnterUser(int roomId, int userNum, String commentContent) {
        ChatCommentVO chatCommentVO = new ChatCommentVO();
        chatCommentVO.setRoomId(roomId);
        chatCommentVO.setCommentUserNum(userNum);
        chatCommentVO.setCommentContent(commentContent);

        chatCommentMapper.saveChatCommentEnterUser(chatCommentVO);
    }

    @Override
    public void addUserToRoom(int roomId, int userNum) {
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
    public boolean isUserInRoom(int roomId, int userNum) {
        return chatJoinMapper.isUserInRoom(roomId, userNum);
    }

    // 특정 채팅방의 읽지 않은 메시지 수 조회
    @Override
    public int getUnreadCount(int roomId, int userNum) {
        return chatCommentMapper.countUnreadMessages(roomId, userNum);
    }

    // 전체 읽지 않은 메시지 수 조회
    @Override
    public int getTotalUnreadCount(int userNum) {
        return chatCommentMapper.countTotalUnreadMessages(userNum);
    }

    // 채팅방 메시지 읽음 처리
    @Override
    public void markAsRead(int roomId, int userNum) {
        chatCommentMapper.updateReadStatus(roomId, userNum);
    }

    @Override
    public int getReceiverUserNum(int roomId, int userNum) {
        return chatJoinMapper.getReceiverUserNum(roomId, userNum);
    }

}
