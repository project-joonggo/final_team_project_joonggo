package com.project.joonggo.repository;

import com.project.joonggo.domain.FileVO;
import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.domain.QnaFileVO;
import com.project.joonggo.domain.QnaVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QnaFileMapper {
    long setQnaId(long qnaId);

    int fileUpload(List<QnaFileVO> flist);

    List<QnaFileVO> getFileList(Long qnaId);

    void deleteFileFromDB(String uuid);

}
