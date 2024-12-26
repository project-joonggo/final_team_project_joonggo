package com.project.joonggo.service;

import com.project.joonggo.domain.*;

import java.util.List;

public interface QnaService {
    long register(QnaVO qnaVO);

    int fileUpload(List<QnaFileVO> flist);

    List<QnaVO> getList(PagingVO pgvo, String pending);

    QnaFileDTO getDetail(Long qnaId);

    String getUpdateContent(Long qnaId);

    void deleteFileFromDB(String uuid);

    void updateQnaContent(QnaVO qnaVO);

    void qnaIsDelUpdate(Long qnaId);

    Long getUserId(Long qnaId);

    Long getMaxQnaId();

    List<QnaVO> getMyList(Long userNum);

    int getTotal(PagingVO pgvo, String pending);
}
