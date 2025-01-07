package com.project.joonggo.service;

import com.project.joonggo.domain.*;

import java.util.List;
import java.util.Map;

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

    int getReportTotal(PagingVO pgvo, Integer reportCompId);

    List<ReportVO> getReportList(PagingVO pgvo, Integer reportCompId);

    void updateReportStatus(Long reportId, String status);

    Long getSellerIdByBoardId(Long boardId);

    List<BoardFileDTO> getRecentProducts();

    List<BoardFileDTO> getRecommendedProducts();

    List<BoardFileDTO> getPopularProducts();

    int getMyTotal(PagingVO pgvo, long userNum);

    List<BoardFileDTO> getMyList(PagingVO pgvo, long userNum);

    List<Map<String, Object>> getAvgPriceForLast15Days(String keyword);

    List<BoardFileDTO> getProductsByCategory(String category, Long boardId);

    void upScore(Long sellerId);

    BoardVO getPopularProduct();
}
