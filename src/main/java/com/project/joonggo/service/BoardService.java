package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;

import java.util.List;

public interface BoardService {
    long register(BoardVO boardVO);

    int fileUpload(List<FileVO> flist);

    List<BoardFileDTO> getList();

    BoardFileDTO getDetail(Long boardId);

    void updateTradeFlag(Long boardId);

    String getUpdateContent(long boardId);


    void updateBoardContent(BoardVO boardVO);

    void deleteFileFromDB(String uuid);

    void boardIsDelUpdate(Long boardId);

    void incrementReadCount(Long boardId);

    void decrementWishCount(Long boardId);

    void incrementWishCount(Long boardId);
}
