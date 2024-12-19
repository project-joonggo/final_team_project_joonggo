package com.project.joonggo.service;

import com.project.joonggo.domain.FileVO;
import com.project.joonggo.domain.QnaFileDTO;
import com.project.joonggo.domain.QnaFileVO;
import com.project.joonggo.domain.QnaVO;

import java.util.List;

public interface QnaService {
    long register(QnaVO qnaVO);

    int fileUpload(List<QnaFileVO> flist);

    List<QnaVO> getList();

    QnaFileDTO getDetail(Long qnaId);

    String getUpdateContent(Long qnaId);

    void deleteFileFromDB(String uuid);

    void updateQnaContent(QnaVO qnaVO);

    void qnaIsDelUpdate(Long qnaId);

    Long getUserId(Long qnaId);

    Long getMaxQnaId();
}
