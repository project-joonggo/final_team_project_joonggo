package com.project.joonggo.repository;

import com.project.joonggo.domain.ChatCommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatCommentMapper {

    // 채팅방에 대한 메세지 목록 가져오기
    List<ChatCommentVO> getCommentsByRoomId(int roomId);

    // 채팅 메세지 저장
    void saveChatComment(ChatCommentVO chatCommentVO);

}
