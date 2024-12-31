package com.project.joonggo.service;


import com.project.joonggo.domain.NotificationVO;
import com.project.joonggo.handler.TimeHandler;
import com.project.joonggo.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate;  // SimpMessagingTemplate 주입

    @Autowired
    private TimeHandler timeHandler;


    // 알림을 DB에 저장하는 메서드
    public void saveNotification(Long userId, String message, Long boardId, String type) {
        NotificationVO notification = new NotificationVO();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus("UNREAD");  // 기본적으로 '읽지 않음' 상태로 설정
        notification.setMoveId(boardId);
        notification.setType(type);  // 타입을 파라미터로 받아서 설정

        log.info(" >>> userId , msg , moveId, type>> > {}, {} , {}, {} " , userId,message,boardId, type);
        log.info(">>> notification >> {}", notification);

        // 알림 DB에 저장
        notificationMapper.insertNotification(notification);

        Long notificationId = notificationMapper.getNotificationId();
        LocalDateTime createAt = notificationMapper.getNotificationCreateAt();

        log.info(" >>>>> notificationsId >>>>> {} ", notificationId);

        // TimeHandler를 사용하여 생성 시간과 현재 시간의 차이를 계산
        String timeAgo = TimeHandler.getTimeAgo(createAt.toString());

        // 알림 메시지에 시간 차이 추가
        String messageWithTime = message + "(" + timeAgo + ")";

//        // DB 저장 후, 웹소켓을 통해 알림을 실시간으로 전송
        sendNotificationToUser(userId, messageWithTime, boardId, notificationId, type);  // 웹소켓을 통해 메시지를 전송

        log.info(">>> message >>>>> {} ", messageWithTime);
    }

    @Override
    public List<NotificationVO> getNotifications(Long userNum) {
        List<NotificationVO> notifications = notificationMapper.getNotifications(userNum);

        // 각 알림에 대해 URL을 생성하여 추가
        for (NotificationVO notification : notifications) {
            log.info(">>> notification >>>> {}" , notification);

            String targetUrl = "";
            if ("SALE".equals(notification.getType())) {
                targetUrl = "/board/detail?boardId=" + notification.getMoveId();  // 구매 관련 알림
            } else if ("REPORT".equals(notification.getType())) {
                targetUrl = "/user/admin";  // 관리자 알림
            } else if ("ANSWER".equals(notification.getType())){
                targetUrl = "/qna/detail?qnaId=" + notification.getMoveId();
            } else if ("QUESTION".equals(notification.getType())){
                targetUrl = "/qna/detail?qnaId=" + notification.getMoveId();
            } else if ("REPLY".equals(notification.getType())){
                targetUrl = "/qna/detail?qnaId=" + notification.getMoveId();
            }
            // 생성된 알림의 시간 차이를 계산
            LocalDateTime createAt = notificationMapper.getCreateAt(notification.getNotificationId());
            String timeAgo = TimeHandler.getTimeAgo(createAt.toString());

            // 경과 시간 추가
            String messageWithTime = notification.getMessage() + " (" + timeAgo + ")";
            notification.setMessage(messageWithTime);


            notification.setUrl(targetUrl);  // URL을 notification 객체에 설정
        }

        return notifications;
    }


    public void markAsRead(Long notificationId) {
        NotificationVO notification = notificationMapper.getNotificationById(notificationId);
        if (notification != null && "UNREAD".equals(notification.getStatus())) {
            notification.setStatus("READ");
            notificationMapper.updateNotificationStatus(notification);
            log.info("Notification marked as read: {}", notificationId);
        }
    }

    public void sendNotificationToUser(Long userId, String message, Long boardId, Long notificationId,String type) {

        String targetUrl = "";  // 기본 URL

        // type에 따라 URL을 다르게 설정
        if ("SALE".equals(type)) {
            targetUrl = "/board/detail?boardId=" + boardId;  // 구매 관련 알림은 /board/detail로 이동
        } else if ("REPORT".equals(type)) {
            targetUrl = "/user/reportList";  // 신고 관련 알림은 /user/reportList로 이동
        } else if ("ANSWER".equals(type)){
            targetUrl = "/qna/detail?qnaId=" + boardId;
        } else if ("QUESTION".equals(type)){
            targetUrl = "/qna/detail?qnaId=" + boardId;
        } else if ("REPLY".equals(type)){
            targetUrl = "/qna/detail?qnaId=" + boardId;
        }

        // 메시지에 HTML 링크 추가
        String htmlMessage = "<a href=\"" + targetUrl + "\" target=\"_blank\">" + message + "</a>";

        log.info("Sending to user:/user/{}/queue/notifications", userId);
        log.info("message >>>>> {} ", htmlMessage);

        // HTML 메시지와 함께 notificationId를 전송
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications",
                "{\"message\": \"" + message + "\", \"url\": \"" + targetUrl + "\", \"notificationId\": \"" + notificationId + "\", \"type\": \"" + type + "\"}");
    }


}
