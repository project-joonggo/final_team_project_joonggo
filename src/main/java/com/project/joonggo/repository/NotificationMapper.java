package com.project.joonggo.repository;

import com.project.joonggo.domain.NotificationVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationVO notification);
}
