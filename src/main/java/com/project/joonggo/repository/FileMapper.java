package com.project.joonggo.repository;

import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    int fileUpload(List<FileVO> flist);

    int setBoardId(long boardId);

    List<FileVO> getFileList(Long boardID);

    void deleteFileFromDB(String uuid);

}
