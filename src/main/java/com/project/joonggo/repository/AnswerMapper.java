package com.project.joonggo.repository;

import com.project.joonggo.domain.AnswerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnswerMapper {
    int post(AnswerVO answerVO);

    List<AnswerVO> getAsList(long qnaId);

    int modify(AnswerVO answerVO);

    int delete(long ano);

    Long getQnaId(long ano);
}
