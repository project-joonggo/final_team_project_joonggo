package com.project.joonggo.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishMapper {
    boolean isAlreadyWished(@Param("userNum") Long userNum, @Param("boardId") Long boardId);

    int removeFromWishList(@Param("userNum") Long userNum, @Param("boardId") Long boardId);

    void addToWishList(@Param("userNum") Long userNum, @Param("boardId") Long boardId);

    List<Long> findBoardIdsByUserNum(Long userNum);
}
