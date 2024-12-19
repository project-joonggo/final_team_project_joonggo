package com.project.joonggo.repository;

import com.project.joonggo.domain.ReplyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    int add(ReplyVO replyVO);

    List<ReplyVO> getReplyList(long ano);

    int deleteRepliesByAno(long ano);

    int modify(ReplyVO replyVO);

    int delete(long replyId);
}
