package com.project.joonggo.service;

import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.repository.BoardMapper;
import com.project.joonggo.repository.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<BoardFileDTO> getList() {
        List<BoardVO> boardList = boardMapper.getBoardList();
        List<BoardFileDTO> boardFileDTOList = new ArrayList<>();
        log.info(">>>boardList >> {}",boardList);

        for (BoardVO boardVO : boardList) {
            // 파일 정보를 가져오고 BoardFileDTO에 넣기
            log.info(">>> boardVO >>> {} ", boardVO);
            List<FileVO> files = fileMapper.getFileList(boardVO.getBoardId());
            log.info(">>> files {}" , files);
            BoardFileDTO boardFileDTO = new BoardFileDTO(boardVO, files);
            boardFileDTOList.add(boardFileDTO);
        }

        return boardFileDTOList;
    }

    @Override
    public BoardFileDTO getDetail(Long boardId) {

        BoardFileDTO boardFileDTO = new BoardFileDTO(boardMapper.getDetail(boardId), fileMapper.getFileList(boardId));

        return boardFileDTO;
    }

    @Override
    public void updateTradeFlag(Long boardId) {
        boardMapper.updateTradeFlag(boardId);
    }

    @Override
    public String getUpdateContent(long boardId) {
        return boardMapper.getUpdateContent(boardId);
    }


    @Override
    public void updateBoardContent(BoardVO boardVO) {
        log.info(">>> bsvimpl boardVO >> {}", boardVO);
        boardMapper.updateBoardContent(boardVO);

        long boardId = boardVO.getBoardId();
        log.info(">>> boardId for file update: {}", boardId);
        fileMapper.setBoardId(boardId);
    }

    @Override
    public void deleteFileFromDB(String uuid) {
        fileMapper.deleteFileFromDB(uuid);
    }

    @Override
    public void boardIsDelUpdate(Long boardId) {
        boardMapper.boardIsDelUpdate(boardId);
    }


}
