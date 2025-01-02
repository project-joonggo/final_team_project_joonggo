package com.project.joonggo.repository;

import com.project.joonggo.domain.ChatJoinVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatJoinMapper {

    // 채팅방에 사용자가 참여했는지 여부 확인
    boolean isUserInRoom(@Param("roomId") int roomId, @Param("userNum") long userNum);

    // 채팅방에 사용자를 추가(입장)
    void addUserToRoom(ChatJoinVO chatJoinVO);

    // 채팅방에 참여한 사용자 목록을 가져오기
    List<ChatJoinVO> getUsersInRoom(int roomId);

    void joinChatRoom(ChatJoinVO chatJoinVO);

    int getReceiverUserNum(@Param("roomId") int roomId, @Param("userNum") long commentUserNum);

    long otherUser(@Param("roomId") int roomId, @Param("userNum") int userNum);

    void deleteUserFromRoom(int roomId, int userNum);

    int countRoomUsers(int roomId);
}
