package com.project.joonggo.service;

import com.project.joonggo.domain.NotificationVO;

import java.util.List;

public interface NotificationService {


    void saveNotification(Long sellerId, String notificationMessage, Long boardId);

    List<NotificationVO> getNotifications(Long userNum);
}
