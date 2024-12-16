package com.project.joonggo.repository;

import com.project.joonggo.domain.NotificationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationVO notification);

    List<NotificationVO> getNotifications(Long userNum);
}
