package com.project.joonggo.repository;

import com.project.joonggo.domain.ChatCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatCommentMapper {

    // 채팅방에 대한 메세지 목록 가져오기
    List<ChatCommentVO> getCommentsByRoomId(int roomId);

    // 채팅 메세지 저장
    void saveChatComment(ChatCommentVO chatCommentVO);

    void saveChatCommentEnterUser(ChatCommentVO chatCommentVO);

    // 특정 채팅방의 읽지 않은 메시지 수 조회
    int countUnreadMessages(@Param("roomId") int roomId, @Param("userNum") long userNum);

    // 전체 읽지 않은 메시지 수 조회
    int countTotalUnreadMessages(@Param("userNum") long userNum);

    // 채팅방 메시지 읽음 처리
    int updateReadStatus(@Param("roomId") int roomId, @Param("userNum") long userNum);

}
