package com.project.joonggo.controller;


import com.project.joonggo.domain.NotificationVO;
import com.project.joonggo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping
    public List<NotificationVO> getNotifications(Principal principal) {

        if (principal == null) {
            return new ArrayList<>(); // 로그인하지 않은 경우 빈 리스트 반환
        }

        Long userNum = Long.valueOf(principal.getName());
        List<NotificationVO> notifications = notificationService.getNotifications(userNum);

        return notifications;
    }


}
