package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationVO {
    private Long notificationId;  // 알림 ID
    private Long userId;          // 사용자 ID (알림을 받을 사용자)
    private String message;       // 알림 메시지
    private String status;        // 읽음 상태 (READ, UNREAD)
    private LocalDateTime createdAt;  // 알림 생성 시간

}
