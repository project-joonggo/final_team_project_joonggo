package com.project.joonggo.controller;


import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.handler.FileDeleteHandler;
import com.project.joonggo.handler.FileHandler;
import com.project.joonggo.handler.ImageHandler;
import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.WishService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
@Slf4j
public class BoardController {
    @Autowired
    private ImageHandler imageHandler;

    private final BoardService boardService;
    private final FileDeleteHandler fileDeleteHandler;
    private final WishService wishService;

    @Autowired
    private FileHandler fileHandler;


    @GetMapping("/register")
    public void register(){}


    @PostMapping("/register")
    public String register(@ModelAttribute BoardVO boardVO, Principal principal){
        log.info("boardVO >>> {} ", boardVO);

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);

        boardVO.setSellerId(userNum); // sellerId가 userNum

        long isOk = boardService.register(boardVO);

        log.info("isOk >>>> {}", isOk);

        return (isOk > 0) ? "/index" : "redirect:/board/register";
    }

    @PostMapping("/multiFileUpload")
    public ResponseEntity<List<FileVO>> multiFileUpload(@RequestParam("files") MultipartFile[] files){

        List<FileVO> flist = null;

        if( files != null && files[0].getSize() > 0){
            flist = fileHandler.uploadFiles(files);
            log.info(">>>> flist >> {} " , flist.toString());
            int isOk = boardService.fileUpload(flist);
        }

        return ResponseEntity.ok(flist);
    }

    @GetMapping("/list")
    public String list(Model model){

        List<BoardFileDTO> list = boardService.getList();

        log.info(">>> list >>> {}", list);

        model.addAttribute("list",list);

        return "/board/list";
    }

    @GetMapping({"/detail","/modify"})
    public void detail(Model model, @RequestParam("boardId") Long boardId, HttpServletRequest request, Principal principal){

        // 현재 요청 URL 확인
        String requestURI = request.getRequestURI();

        // 'detail' 페이지에서만 조회수를 증가시킴
        if (requestURI.contains("/detail")) {
            boardService.incrementReadCount(boardId);  // 조회수 증가
        }

        // 로그인된 사용자 정보 가져오기

        Long userNum = principal != null ?  Long.valueOf(principal.getName()) : null;

        log.info(">>> userNum >>> {}", userNum);

        // 로그인한 사용자만 찜 상태 확인
        boolean isAlreadyWished = false;
        if (userNum != null) {
            // 사용자가 찜한 상태인지 확인
            isAlreadyWished = wishService.isAlreadyWished(userNum, boardId);
        }
        log.info(">>>> isAl Wish >> {}" , isAlreadyWished);

        BoardFileDTO boardFileDTO = boardService.getDetail(boardId);

        model.addAttribute("boardFileDTO", boardFileDTO);
        model.addAttribute("isAlreadyWished", isAlreadyWished);

    }


    @PostMapping("/update")
    public String update(@ModelAttribute BoardVO boardVO){

        if (boardVO.getTradeFlag() == 1) {
            log.info(">>> 거래 완료된 게시글은 수정할 수 없습니다.");
            return "redirect:/board/detail?boardId=" + boardVO.getBoardId(); // 수정 불가능한 페이지로 리다이렉트, 사용자한테는 js에서 alert로 경고문
        }

        log.info(">>>> boardVO >> {}", boardVO);
        // 수정된 컨텐츠 가져와서 이미지 url만 빼기
        List<String> existingImages = imageHandler.extractImageUrls(boardVO.getBoardContent());

        // 기존 이미지 목록과 비교하여 삭제된 이미지들을 추출
        List<String> updatedImages = imageHandler.extractImageUrls(boardService.getUpdateContent(boardVO.getBoardId())); // 서버에서 파일에 저장돼있는 정보를 가져옴
        log.info(">>> updateImages >> {}" , updatedImages);
        List<String> imagesToDelete = new ArrayList<>(updatedImages);
        log.info(">>> imagesToDelete1 >> {} " , imagesToDelete);

        imagesToDelete.removeAll(existingImages);

        log.info(">>> imagesToDelete2 >> {} " , imagesToDelete);

        for (String imageUrl : imagesToDelete) {
            log.info(">>> imageUrl >> {}" , imageUrl);
            String uuid = imageHandler.extractUuidFromUrl(imageUrl);  // 이미지 URL에서 UUID 추출
            log.info(">>> uuid >>> {}" , uuid);
            if (uuid != null) {
                String imageFileName = imageHandler.extractImageFileName(imageUrl);
                log.info(">>> imageFileName ?? > {}" , imageFileName);
                String saveDir = imageHandler.extractSaveDir(imageUrl);
                log.info(">>> saveDir ?? > {}", saveDir);
                // 실제파일 삭제
                fileDeleteHandler.deleteFile(saveDir,uuid,imageFileName);
                // DB에서 파일 정보 삭제 ( is_del = 'Y')
                boardService.deleteFileFromDB(uuid);
            }
        }

        String updatedContent = imageHandler.removeImageUrlsFromContent(boardVO.getBoardContent(), imagesToDelete);

        log.info("updateContent >>>> {}" , updatedContent);

        boardVO.setBoardContent(updatedContent);

        log.info("boardDTO.getUpdateContent >>>> {}" , boardVO.getBoardContent());

        boardService.updateBoardContent(boardVO);

        return  "redirect:/board/detail?boardId=" + boardVO.getBoardId();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("boardId") Long boardId){

        boardService.boardIsDelUpdate(boardId);

        return "redirect:/board/list";
    }

}
