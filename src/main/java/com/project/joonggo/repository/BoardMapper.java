package com.project.joonggo.repository;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.PagingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    long register(BoardVO boardVO);

    long getBoardId(BoardVO boardVO);

    List<BoardFileDTO> getList(PagingVO pgvo);

    BoardVO getDetail(Long boardId);

    void updateTradeFlag(Long boardId);

    String getUpdateContent(long boardId);

    void updateBoardContent(BoardVO boardVO);

    void boardIsDelUpdate(Long boardId);

    List<BoardVO> getBoardList(PagingVO pgvo);

    void incrementReadCount(Long boardId);

    void decrementWishCount(Long boardId);

    void incrementWishCount(Long boardId);

    int getLikeCount(Long boardId);

    int getTotal(PagingVO pgvo);

    List<BoardVO> searchPrice(String keyword);

    Long getSellerIdByBoardId(Long boardId);

    List<BoardVO> getRecentProducts();

    List<BoardVO> getRecommendedProducts();

    List<BoardVO> getPopularProducts();

    int getMyTotal(PagingVO pgvo, long userNum);

    List<BoardVO> getMyBoardList(@Param("pgvo") PagingVO pgvo,
                                 @Param("userNum") long userNum);

    String getCategory(Long boardId);

    List<Map<String, Object>> getAvgPriceForLast15Days(String keyword);
}
