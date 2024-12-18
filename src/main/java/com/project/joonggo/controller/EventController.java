package com.project.joonggo.controller;

import com.project.joonggo.domain.BoardFileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event/*")
@Slf4j
public class EventController {

    @GetMapping("/event/list")
    public String showEventList() {

        return "event/list";  // 이벤트 리스트 페이지로 이동
    }

    @GetMapping("/event/roulette")
    public String showRouletteEvent() {

        return "event/roulette";  // 룰렛 페이지로 이동
    }

}
