package com.project.joonggo.repository;

import com.project.joonggo.domain.ChatRoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    List<ChatRoomVO> getChatRoomList(@Param("userNum") int userNum);

    void createChatRoom(ChatRoomVO chatRoomVO);

    ChatRoomVO findExistingRoom(@Param("sellerId") int sellerId, @Param("roomName") String roomName);
}
