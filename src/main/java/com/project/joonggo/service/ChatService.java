package com.project.joonggo.service;

import com.project.joonggo.domain.ChatCommentVO;
import com.project.joonggo.domain.ChatRoomVO;

import java.util.List;

public interface ChatService {

    List<ChatRoomVO> getChatRoomList(int userNum);

    List<ChatCommentVO> getCommentsByRoomId(int roomId);

    void saveChatComment(int roomId, int userNum, String messageContent);

    boolean isUserInRoom(int roomId, int userNum);

    void addUserToRoom(int roomId, int userNum);
}
