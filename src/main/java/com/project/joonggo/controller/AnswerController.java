package com.project.joonggo.controller;


import com.project.joonggo.domain.AnswerVO;
import com.project.joonggo.service.AnswerService;
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

    @ResponseBody
    @PostMapping("/post")
    public String post(@RequestBody AnswerVO answerVO){

        int isOk = answerService.post(answerVO);

        return isOk > 0 ? "1" : "0";
    }


    @ResponseBody
    @GetMapping("/list/{qnaId}")
    public List<AnswerVO> list(@PathVariable("qnaId") long qnaId){


        List<AnswerVO> list = answerService.getAsList(qnaId);

        log.info(">>> list >> {}" , list);

        return list;
    }
}
