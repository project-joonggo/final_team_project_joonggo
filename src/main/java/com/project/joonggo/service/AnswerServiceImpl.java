package com.project.joonggo.service;


import com.project.joonggo.domain.AnswerVO;
import com.project.joonggo.repository.AnswerMapper;
import com.project.joonggo.repository.QnaMapper;
import com.project.joonggo.repository.ReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService{
    private final AnswerMapper answerMapper;
    private final ReplyMapper replyMapper;
    private final QnaMapper qnaMapper;

    @Override
    public int post(AnswerVO answerVO) {

        log.info(">>>> answerVO >>> {}" , answerVO);

        qnaMapper.updateAnswerCount(answerVO.getQnaId());

        return answerMapper.post(answerVO);
    }

    @Override
    public List<AnswerVO> getAsList(long qnaId) {
        return answerMapper.getAsList(qnaId);
    }

    @Override
    public int modify(AnswerVO answerVO) {
        return answerMapper.modify(answerVO);
    }

    @Override
    public int delete(long ano) {
        return answerMapper.delete(ano);
    }

    @Override
    public Long getQnaId(long ano) {
        int delOk = replyMapper.deleteRepliesByAno(ano);

        return answerMapper.getQnaId(ano);
    }
}
