package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.repository.BoardMapper;
import com.project.joonggo.repository.FileMapper;
import com.project.joonggo.repository.WishMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService{
    private final WishMapper wishMapper;
    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;

    @Override
    public boolean isAlreadyWished(Long userNum, Long boardId) {
        return wishMapper.isAlreadyWished(userNum, boardId);
    }

    @Override
    public int removeFromWishList(Long userNum, Long boardId) {
        return wishMapper.removeFromWishList(userNum, boardId);
    }

    @Override
    public void addToWishList(Long userNum, Long boardId) {
        wishMapper.addToWishList(userNum, boardId);
    }

    @Override
    public List<BoardFileDTO> getWishedProducts(Long userNum) {
        List<Long> boardIds = wishMapper.findBoardIdsByUserNum(userNum);

        // board_id들을 이용해 상품 정보와 이미지 파일들을 가져옵니다.
        List<BoardFileDTO> wishedProducts = new ArrayList<>();

        for (Long boardId : boardIds) {
            BoardVO boardVO = boardMapper.getDetail(boardId);
            List<FileVO> fileVOList = fileMapper.getFileList(boardId);
            wishedProducts.add(new BoardFileDTO(boardVO, fileVOList));
        }
        log.info(">>> wishedProducts >> {}", wishedProducts);

        return wishedProducts;
    }
}
