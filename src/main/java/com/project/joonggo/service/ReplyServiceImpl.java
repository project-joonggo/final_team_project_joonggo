package com.project.joonggo.service;

import com.project.joonggo.domain.ReplyVO;
import com.project.joonggo.repository.ReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService{
    private final ReplyMapper replyMapper;

    @Override
    public int add(ReplyVO replyVO) {
        return replyMapper.add(replyVO);
    }

    @Override
    public List<ReplyVO> getReplyList(long ano) {
        return replyMapper.getReplyList(ano);
    }

    @Override
    public int modify(ReplyVO replyVO) {
        return replyMapper.modify(replyVO);
    }

    @Override
    public int delete(long replyId) {
        return replyMapper.delete(replyId);
    }
}
