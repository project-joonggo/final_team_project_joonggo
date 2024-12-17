package com.project.joonggo.service;

import com.project.joonggo.domain.*;
import com.project.joonggo.repository.BoardMapper;
import com.project.joonggo.repository.FileMapper;
import com.project.joonggo.repository.ReportMapper;
import com.project.joonggo.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;
    private final ReportMapper reportMapper;
    private final UserMapper userMapper;

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
    public List<BoardFileDTO> getList(PagingVO pgvo) {
        List<BoardVO> boardList = boardMapper.getBoardList(pgvo);
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

    @Override
    public void incrementReadCount(Long boardId) {
        boardMapper.incrementReadCount(boardId);
    }

    @Override
    public void decrementWishCount(Long boardId) {
        boardMapper.decrementWishCount(boardId);
    }

    @Override
    public void incrementWishCount(Long boardId) {
        boardMapper.incrementWishCount(boardId);
    }

    @Override
    public int getLikeCount(Long boardId) {
        return boardMapper.getLikeCount(boardId);
    }

    @Override
    public int getTotal(PagingVO pgvo) {
        return boardMapper.getTotal(pgvo);
    }

    @Override
    public List<BoardFileDTO> searchPrice(String keyword) {
        List<BoardVO> boardList = boardMapper.searchPrice(keyword);

        List<BoardFileDTO> boardFileDTOList = new ArrayList<>();

        for (BoardVO boardVO : boardList) {
            List<FileVO> files = fileMapper.getFileList(boardVO.getBoardId());
            BoardFileDTO boardFileDTO = new BoardFileDTO(boardVO, files);
            boardFileDTOList.add(boardFileDTO);
        }

        return boardFileDTOList;
    }

    @Override
    public Long getSellerIdByBoardId(Long boardId) {
        return boardMapper.getSellerIdByBoardId(boardId);
    }

    @Override
    public List<ReasonVO> getReasonList() {
        return reportMapper.getReasonList();
    }

    @Override
    public void saveReport(ReportVO reportVO) {
        reportMapper.insertReport(reportVO);
    }

    @Override
    public int getReportTotal(PagingVO pgvo) {
        return reportMapper.getTotalCount(pgvo);
    }

    @Override
    public List<ReportVO> getReportList(PagingVO pgvo) {
        List<ReportVO> reportList = reportMapper.getList(pgvo);
        // 각 보고서에 대해 신고 사유를 추가
        for (ReportVO reportVO : reportList) {
            String compContent = reportMapper.getCompContentByCompId(reportVO.getReportCompId());
            reportVO.setCompContent(compContent);
        }

        return reportList;
    }

    @Override
    @Transactional
    public void updateReportStatus(Long reportId, String status) {
        String currentStatus = reportMapper.getStatusByReportId(reportId);

        reportMapper.updateStatus(reportId, status);
        String newStatus = reportMapper.getStatusByReportId(reportId);

        Long userNum = reportMapper.getUserNumByReportId(reportId);

        if ("confirmed".equals(currentStatus) && !"confirmed".equals(newStatus)) {
            // CONFIRMED → PENDING or CANCELED: 점수 복구 (+1)
            userMapper.upScore(userNum);  // 점수 증가
        } else if (!"confirmed".equals(currentStatus) && "confirmed".equals(newStatus)) {
            // PENDING or CANCELED → CONFIRMED: 점수 감소 (-1)
            userMapper.downScore(userNum);  // 점수 감소
        }
    }

}
