package com.project.joonggo.controller;

import com.project.joonggo.service.EventService;
import jdk.jfr.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event/*")
@Slf4j
public class EventController {
    private final EventService eventService;

    @GetMapping("/event/list")
    public String showEventList() {

        return "event/list";  // 이벤트 리스트 페이지로 이동
    }

    @GetMapping("/event/roulette")
    public String showRouletteEvent() {

        return "event/roulette";  // 룰렛 페이지로 이동
    }
    @PostMapping("/event/roulette")
    public ResponseEntity<?> handleRouletteResult(@RequestBody Map<String, String> request, Principal principal) {
        String result = request.get("result");
        Long userId = Long.parseLong(principal.getName()); // 로그인한 유저의 ID

        try {
            eventService.processRouletteResult(userId, result);
            return ResponseEntity.ok(Map.of("success", true, "message", "룰렛 결과 처리 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

}
