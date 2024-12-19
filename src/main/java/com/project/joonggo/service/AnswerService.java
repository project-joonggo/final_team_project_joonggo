package com.project.joonggo.service;

import com.project.joonggo.domain.AnswerVO;

import java.util.List;

public interface AnswerService {
    int post(AnswerVO answerVO);

    List<AnswerVO> getAsList(long qnaId);

    int modify(AnswerVO answerVO);

    int delete(long ano);

    Long getQnaId(long ano);
}
