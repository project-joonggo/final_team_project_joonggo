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
    public void saveChatComment(int roomId, int userNum, String messageContent) {
        ChatCommentVO chatCommentVO = new ChatCommentVO();
        chatCommentVO.setRoomId(roomId);
        chatCommentVO.setCommentUserNum(userNum);
        chatCommentVO.setCommentContent(messageContent);
        chatCommentMapper.saveChatComment(chatCommentVO);
    }

    @Override
    public void addUserToRoom(int roomId, int userNum) {
        ChatJoinVO chatJoinVO = new ChatJoinVO();
        chatJoinVO.setRoomId(roomId);
        chatJoinVO.setUserNum(userNum);
        chatJoinMapper.addUserToRoom(chatJoinVO);
    }

    @Override
    public boolean isUserInRoom(int roomId, int userNum) {
        return chatJoinMapper.isUserInRoom(roomId, userNum);
    }
}
