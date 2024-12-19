package com.project.joonggo.controller;


import com.project.joonggo.domain.AnswerVO;
import com.project.joonggo.service.AnswerService;
import com.project.joonggo.service.NotificationService;
import com.project.joonggo.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answer/*")
@Slf4j
public class AnswerController {
    private final AnswerService answerService;
    private final NotificationService notificationService;
    private final QnaService qnaService;

    @ResponseBody
    @PostMapping("/post")
    public String post(@RequestBody AnswerVO answerVO){

        log.info(">>>> post avo >> {}", answerVO);

        int isOk = answerService.post(answerVO);


        Long userId = qnaService.getUserId(answerVO.getQnaId());

        log.info(">>>> post userId >>> {}" ,userId);

        // 알림 메시지 생성
        String notificationMessage = "질문에 대한 답변이 등록되었습니다. 확인해보세요!!." ;

        // 알림을 관리자에게 보내기
        notificationService.saveNotification(userId, notificationMessage, answerVO.getQnaId(), "ANSWER");



        return isOk > 0 ? "1" : "0";
    }


    @ResponseBody
    @GetMapping("/list/{qnaId}")
    public List<AnswerVO> list(@PathVariable("qnaId") long qnaId){


        List<AnswerVO> list = answerService.getAsList(qnaId);

        log.info(">>> list >> {}" , list);

        return list;
    }


    @ResponseBody
    @PutMapping("/modify")
    public String modify(@RequestBody AnswerVO answerVO){

        log.info(">>>> mod avo >>> {}" , answerVO);

        int isOK = answerService.modify(answerVO);

        return isOK > 0 ? "1" : "0";
    }


    @ResponseBody
    @DeleteMapping("/delete/{ano}")
    public String delete(@PathVariable("ano") long ano){

        log.info(">>> ano >>> {}", ano);

        int isOk = answerService.delete(ano);

        return isOk > 0 ? "1" : "0";
    }



}
