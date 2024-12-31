package com.project.joonggo.controller;


import com.project.joonggo.domain.AnswerVO;
import com.project.joonggo.domain.ReplyVO;
import com.project.joonggo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reply/*")
public class ReplyController {
    private final ReplyService replyService;
    private final LoginService loginService;
    private final NotificationService notificationService;
    private final AnswerService answerService;
    private final QnaService qnaService;

    // 대댓글 추가
    @ResponseBody
    @PostMapping("/add")
    public String add(@RequestBody ReplyVO replyVO) {
        log.info(">>>> add replyVO >>> {}", replyVO);

        Long adminId = loginService.getAdminId();

        Long qnaId = answerService.getQnaId(replyVO.getAno());

        Long userId = qnaService.getUserId(qnaId);

        log.info(">>>>> adId , qaId ,userId >> {}, {}, {}" , adminId,qnaId, userId);

        // 작성자 이름을 설정
        if (replyVO.getUserNum() != adminId) {
            replyVO.setWriterName(loginService.getUserName(replyVO.getUserNum()));  // 유저 이름 가져오기
            log.info(">>> userName >> {}", replyVO.getWriterName());
        } else {
            replyVO.setWriterName("관리자");  // 관리자 이름 설정
        }

        // 대댓글 추가
        int isOk = replyService.add(replyVO);


        String notificationMessage = "";
        if(replyVO.getUserNum() != adminId){
            notificationMessage = "<span>문의</span>답변에 추가질문이 달렸습니다.<br> 확인하고 추가적으로 답변을해주세요.";
            notificationService.saveNotification(adminId, notificationMessage, qnaId, "REPLY");
        } else {
            notificationMessage = "<span>문의</span>추가질문에 답변이 달렸습니다.<br> 확인해보세요!";
            notificationService.saveNotification(userId, notificationMessage, qnaId, "REPLY");
        }



        return isOk > 0 ? "1" : "0";
    }

    // 특정 답변에 대한 대댓글 조회
    @ResponseBody
    @GetMapping("/list/{ano}")
    public List<ReplyVO> list(@PathVariable("ano") long ano) {
        List<ReplyVO> list = replyService.getReplyList(ano);

        log.info(">>> list >> {}", list);

        return list;
    }

    @ResponseBody
    @PutMapping("/modify")
    public String modify(@RequestBody ReplyVO replyVO){

        log.info(">>>> mod rvo >>> {}" , replyVO);

        int isOK = replyService.modify(replyVO);

        return isOK > 0 ? "1" : "0";
    }

    @ResponseBody
    @DeleteMapping("/delete/{replyId}")
    public String delete(@PathVariable("replyId") long replyId){

        log.info(">>> replyId >>> {}", replyId);

        int isOk = replyService.delete(replyId);

        return isOk > 0 ? "1" : "0";
    }

}
