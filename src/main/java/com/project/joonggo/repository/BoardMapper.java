package com.project.joonggo.repository;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    long register(BoardVO boardVO);

    long getBoardId(BoardVO boardVO);

    List<BoardFileDTO> getList();

    BoardVO getDetail(Long boardId);

    void updateTradeFlag(Long boardId);

    String getUpdateContent(long boardId);

    void updateBoardContent(BoardVO boardVO);

    void boardIsDelUpdate(Long boardId);

    List<BoardVO> getBoardList();

    void incrementReadCount(Long boardId);

    void decrementWishCount(Long boardId);

    void incrementWishCount(Long boardId);
}
