package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.repository.BoardMapper;
import com.project.joonggo.repository.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;

    @Override
    public long register(BoardVO boardVO) {

        long isOk = boardMapper.register(boardVO);

        if(isOk > 0) {
            long boardId = boardMapper.getBoardId(boardVO);
            log.info(">>> boardID>> {}", boardId);
            isOk = fileMapper.setBoardId(boardId);
        }
        return isOk;
    }

    @Override
    public int fileUpload(List<FileVO> flist) {

        log.info(">>> flist >>>>>> {}",flist);
        return fileMapper.fileUpload(flist);
    }

    @Override
    public List<BoardVO> getList() {
        return boardMapper.getList();
    }

    @Override
    public BoardFileDTO getDetail(Long boardID) {

        BoardFileDTO boardFileDTO = new BoardFileDTO(boardMapper.getDetail(boardID), fileMapper.getFileList(boardID));

        return boardFileDTO;
    }


}
