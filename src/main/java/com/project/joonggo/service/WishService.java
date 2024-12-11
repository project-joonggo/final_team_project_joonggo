package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;

import java.util.List;

public interface WishService {
    boolean isAlreadyWished(Long userNum, Long boardId);

    int removeFromWishList(Long userNum, Long boardId);

    void addToWishList(Long userNum, Long boardId);

    List<BoardFileDTO> getWishedProducts(Long userNum);
}
