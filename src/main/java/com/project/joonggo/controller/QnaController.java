package com.project.joonggo.controller;


import com.project.joonggo.domain.*;
import com.project.joonggo.handler.FileDeleteHandler;
import com.project.joonggo.handler.ImageHandler;
import com.project.joonggo.handler.QnaFileHandler;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.NotificationService;
import com.project.joonggo.service.QnaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/qna/*")
public class QnaController {

    private final QnaService qnaService;
    private final QnaFileHandler qnaFileHandler;
    private final ImageHandler imageHandler;
    private final FileDeleteHandler fileDeleteHandler;
    private final LoginService loginService;
    private final NotificationService notificationService;


    @GetMapping("/main")
    public void moveMain(){}

    @GetMapping("/register")
    public String register(Principal principal){
        if(principal == null){
            return "/user/login";
        }
        return "/qna/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@ModelAttribute QnaVO qnaVO , Principal principal){
        log.info("qnaVO >>> {} ", qnaVO);

        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);

        qnaVO.setUserId(userNum); // 글을 올린 userId

        long isOk = qnaService.register(qnaVO);

        log.info("isOk >>>> {}", isOk);

        Long qnaId = qnaService.getMaxQnaId();

        Long adminId = loginService.getAdminId();

        // 알림 메시지 생성
        String notificationMessage = "새로운 질문이 접수되었습니다. " + qnaVO.getCategory() + " 관련 답변을 남겨주세요." ;

        // 알림을 관리자에게 보내기
        notificationService.saveNotification(adminId, notificationMessage, qnaId, "QUESTION");


        return (isOk > 0) ? "1" : "0";
    }

    @PostMapping("/multiFileUpload")
    public ResponseEntity<List<QnaFileVO>> multiFileUpload(@RequestParam("files") MultipartFile[] files){

        List<QnaFileVO> flist = null;

        if( files != null && files[0].getSize() > 0){
            flist = qnaFileHandler.uploadFiles(files);
            log.info(">>>> flist >> {} " , flist.toString());
            int isOk = qnaService.fileUpload(flist);
        }

        return ResponseEntity.ok(flist);
    }

    @GetMapping("/list")
    public String getList(Model model){

        List<QnaVO> list = qnaService.getList();

        model.addAttribute("list", list);

        return "/qna/list";
    }

    @GetMapping("/my")
    public String getMy(Model model, Principal principal){

        Long userNum = Long.valueOf(principal.getName());

        List<QnaVO> list = qnaService.getMyList(userNum);
        model.addAttribute("list",list);

        return "/qna/my";
    }

    @GetMapping({"/detail","/modify"})
    public void detail(Model model, @RequestParam("qnaId") Long qnaId, HttpServletRequest request, Principal principal){

        // 현재 요청 URL 확인
        String requestURI = request.getRequestURI();

        // 로그인된 사용자 정보 가져오기
        Long userNum = principal != null ?  Long.valueOf(principal.getName()) : null;

        log.info(">>> userNum >>> {}", userNum);

        QnaFileDTO qnaFileDTO = qnaService.getDetail(qnaId);

        log.info(">>> qnaFileDTO >>> {}" , qnaFileDTO);

        model.addAttribute("qnaFileDTO", qnaFileDTO);

    }

    @PostMapping("/update")
    public String update(@ModelAttribute QnaVO qnaVO){

        log.info(">>>> QnaVO >> {}", qnaVO);

        // 수정된 컨텐츠 가져와서 이미지 url만 빼기
        List<String> existingImages = imageHandler.extractImageUrls(qnaVO.getQnaContent());

        // 기존 이미지 목록과 비교하여 삭제된 이미지들을 추출
        List<String> updatedImages = imageHandler.extractImageUrls(qnaService.getUpdateContent(qnaVO.getQnaId())); // 서버에서 파일에 저장돼있는 정보를 가져옴
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
                // DB에서 파일 정보 삭제
                qnaService.deleteFileFromDB(uuid);
            }
        }

        String updatedContent = imageHandler.removeImageUrlsFromContent(qnaVO.getQnaContent(), imagesToDelete);

        log.info("updateContent >>>> {}" , updatedContent);

        qnaVO.setQnaContent(updatedContent);

        log.info("boardDTO.getUpdateContent >>>> {}" , qnaVO.getQnaContent());

        qnaService.updateQnaContent(qnaVO);


        return  "redirect:/qna/detail?qnaId=" + qnaVO.getQnaId();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("qnaId") Long qnaId){

        qnaService.qnaIsDelUpdate(qnaId);

        return "redirect:/qna/list";
    }


}
