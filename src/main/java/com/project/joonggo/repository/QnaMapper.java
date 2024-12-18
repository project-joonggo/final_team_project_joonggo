package com.project.joonggo.repository;


import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.QnaVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QnaMapper {
    long register(QnaVO qnaVO);

    long getQnaId(QnaVO qnaVO);

    QnaVO getDetail(Long qnaId);

    String getUpdateContent(Long qnaId);

    void updateQnaContent(QnaVO qnaVO);

    void qnaIsDelUpdate(Long qnaId);
}
