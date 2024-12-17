package com.project.joonggo.service;


import com.project.joonggo.domain.NotificationVO;
import com.project.joonggo.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate;  // SimpMessagingTemplate 주입


    // 알림을 DB에 저장하는 메서드
    public void saveNotification(Long userId, String message, Long boardId) {
        NotificationVO notification = new NotificationVO();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus("UNREAD");  // 기본적으로 '읽지 않음' 상태로 설정

        log.info(" >>> userId , msg >> > {}, {}" , userId,message);
        log.info(">>> notification >> {}", notification);

        // 알림 DB에 저장
        notificationMapper.insertNotification(notification);

//        // DB 저장 후, 웹소켓을 통해 알림을 실시간으로 전송
        sendNotificationToUser(userId, message, boardId);  // 웹소켓을 통해 메시지를 전송

        log.info(">>> message >>>>> {} ", message);
    }

    @Override
    public List<NotificationVO> getNotifications(Long userNum) {
        return notificationMapper.getNotifications(userNum);
    }

    public void sendNotificationToUser(Long userId, String message, Long boardId) {
        String boardUrl = "/board/detail?boardId=" + boardId;  // 게시글 페이지 URL 생성

        // 메시지에 HTML 링크 추가
        String htmlMessage = "<a href=\"" + boardUrl + "\" target=\"_blank\">" + message + "</a>";

        log.info("Sending to user:/user/{}/queue/notifications", userId);
        log.info("message >>>>> {} ", htmlMessage);

        // HTML 메시지를 그대로 JSON 형식으로 전송
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications",
                "{\"message\": \"" + htmlMessage + "\"}");
    }

}
