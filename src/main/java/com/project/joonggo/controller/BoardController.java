package com.project.joonggo.controller;


import com.project.joonggo.domain.BoardFileDTO;
import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.FileVO;
import com.project.joonggo.handler.FileDeleteHandler;
import com.project.joonggo.handler.FileHandler;
import com.project.joonggo.handler.ImageHandler;
import com.project.joonggo.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private FileHandler fileHandler;


    @GetMapping("/register")
    public void register(){}


    @PostMapping("/register")
    public String register(@ModelAttribute BoardVO boardVO){
        log.info("boardVO >>> {} ", boardVO);

        long isOk = boardService.register(boardVO);

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

        List<BoardVO> list = boardService.getList();

        log.info(">>> list >>> {}", list);

        model.addAttribute("list",list);

        return "/board/list";
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("boardID") Long boardID){

        BoardFileDTO boardFileDTO = boardService.getDetail(boardID);

        model.addAttribute("boardFileDTO", boardFileDTO);

    }





}
