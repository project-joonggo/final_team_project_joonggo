package com.project.joonggo.service;


import com.project.joonggo.domain.NotificationVO;
import com.project.joonggo.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;


    // 알림을 DB에 저장하는 메서드
    public void saveNotification(Long userId, String message) {
        NotificationVO notification = new NotificationVO();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus("UNREAD");  // 기본적으로 '읽지 않음' 상태로 설정

        log.info(" >>> userId , msg >> > {}, {}" , userId,message);
        log.info(">>> notification >> {}", notification);

        // 알림 DB에 저장
        notificationMapper.insertNotification(notification);

        // 웹소켓을 통해 알림을 실시간으로 전송
        sendNotificationToUser(userId, message);
    }

    // 특정 사용자에게 웹소켓 알림을 보내는 메서드
    public void sendNotificationToUser(Long userId, String message) {
        String destination = "/user/" + userId + "/queue/notifications";  // 사용자의 큐로 알림 전송
        log.info("Sending to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, message);
    }

}
