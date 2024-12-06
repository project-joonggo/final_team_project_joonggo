package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;

import java.util.List;

public interface BoardService {
    long register(BoardVO boardVO);

    int fileUpload(List<FileVO> flist);

    List<BoardVO> getList();

    BoardFileDTO getDetail(Long boardID);
}
