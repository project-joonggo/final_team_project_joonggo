package com.project.joonggo.service;

import com.project.joonggo.domain.*;

import java.util.List;

public interface BoardService {
    long register(BoardVO boardVO);

    int fileUpload(List<FileVO> flist);

    List<BoardFileDTO> getList(PagingVO pgvo);

    BoardFileDTO getDetail(Long boardId);

    void updateTradeFlag(Long boardId);

    String getUpdateContent(long boardId);


    void updateBoardContent(BoardVO boardVO);

    void deleteFileFromDB(String uuid);

    void boardIsDelUpdate(Long boardId);

    void incrementReadCount(Long boardId);

    void decrementWishCount(Long boardId);

    void incrementWishCount(Long boardId);

    int getLikeCount(Long boardId);

    int getTotal(PagingVO pgvo);

    List<BoardFileDTO> searchPrice(String keyword);

    List<ReasonVO> getReasonList();

    void saveReport(ReportVO reportVO);

    int getReportTotal(PagingVO pgvo);

    List<ReportVO> getReportList(PagingVO pgvo);

    void updateReportStatus(Long reportId, String status);
}
