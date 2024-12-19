package com.project.joonggo.service;

import com.project.joonggo.domain.ReplyVO;

import java.util.List;

public interface ReplyService {
    int add(ReplyVO replyVO);

    List<ReplyVO> getReplyList(long ano);

    int modify(ReplyVO replyVO);

    int delete(long replyId);
}
