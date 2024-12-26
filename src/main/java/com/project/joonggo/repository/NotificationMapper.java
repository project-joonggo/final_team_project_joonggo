package com.project.joonggo.repository;

import com.project.joonggo.domain.NotificationVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationVO notification);

    List<NotificationVO> getNotifications(Long userNum);

    NotificationVO getNotificationById(Long notificationId);

    void updateNotificationStatus(NotificationVO notification);

    Long getNotificationId();

    LocalDateTime getNotificationCreateAt();

    LocalDateTime getCreateAt(Long notificationId);
}
