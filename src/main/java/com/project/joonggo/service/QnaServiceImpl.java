package com.project.joonggo.service;


import com.project.joonggo.domain.*;
import com.project.joonggo.repository.QnaFileMapper;
import com.project.joonggo.repository.QnaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaServiceImpl implements QnaService{

    private final QnaMapper qnaMapper;
    private final QnaFileMapper qnaFileMapper;

    @Override
    public long register(QnaVO qnaVO) {
        long isOk = qnaMapper.register(qnaVO);

        if(isOk > 0) {
            long qnaId = qnaMapper.getQnaId(qnaVO);
            log.info(">>> qnaId>> {}", qnaId);
            qnaFileMapper.setQnaId(qnaId);
        }

        return isOk;

    }

    @Override
    public int fileUpload(List<QnaFileVO> flist) {
        log.info(">>> flist >>>>>> {}",flist);

        return qnaFileMapper.fileUpload(flist);
    }

    @Override
    public List<QnaVO> getList(PagingVO pgvo, String pending) {
        // "true"이면 답변대기 중인 항목만 가져오고, "false"이면 전체 가져옴
        if ("true".equals(pending)) {
            return qnaMapper.getListPending(pgvo);  // 답변대기 필터링
        } else {
            return qnaMapper.getList(pgvo);  // 전체 리스트 가져오기
        }
    }

    @Override
    public QnaFileDTO getDetail(Long qnaId) {
        QnaFileDTO qnaFileDTO = new QnaFileDTO(qnaMapper.getDetail(qnaId), qnaFileMapper.getFileList(qnaId));

        return qnaFileDTO;
    }

    @Override
    public String getUpdateContent(Long qnaId) {
        return qnaMapper.getUpdateContent(qnaId);
    }

    @Override
    public void deleteFileFromDB(String uuid) {
        qnaFileMapper.deleteFileFromDB(uuid);
    }

    @Override
    public void updateQnaContent(QnaVO qnaVO) {
        log.info(">>>  qnaVO >> {}", qnaVO);
        qnaMapper.updateQnaContent(qnaVO);

        long qnaId = qnaVO.getQnaId();
        log.info(">>> boardId for file update: {}", qnaId);
        qnaFileMapper.setQnaId(qnaId);
    }

    @Override
    public void qnaIsDelUpdate(Long qnaId) {
        qnaMapper.qnaIsDelUpdate(qnaId);
    }

    @Override
    public Long getUserId(Long qnaId) {
        return qnaMapper.getUserId(qnaId);
    }

    @Override
    public Long getMaxQnaId() {
        return qnaMapper.getMaxQnaId();
    }

    @Override
    public List<QnaVO> getMyList(Long userNum) {
        return qnaMapper.getMyList(userNum);
    }

    @Override
    public int getTotal(PagingVO pgvo, String pending) {
        if ("true".equals(pending)) {
            // 답변대기 중인 항목만 필터링하여 카운트
            return qnaMapper.countPending(pgvo);
        } else {
            // 전체 카운트
            return qnaMapper.getTotal(pgvo);
        }
    }


}
