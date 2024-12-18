package com.project.joonggo.service;


import com.project.joonggo.domain.AnswerVO;
import com.project.joonggo.repository.AnswerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService{
    private final AnswerMapper answerMapper;

    @Override
    public int post(AnswerVO answerVO) {
        return answerMapper.post(answerVO);
    }

    @Override
    public List<AnswerVO> getAsList(long qnaId) {
        return answerMapper.getAsList(qnaId);
    }
}
