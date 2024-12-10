package com.project.joonggo.repository;

import com.project.joonggo.domain.BoardVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    long register(BoardVO boardVO);

    long getBoardId(BoardVO boardVO);

    List<BoardVO> getList();

    BoardVO getDetail(Long boardID);
}
