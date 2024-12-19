package com.project.joonggo.repository;

import com.project.joonggo.domain.EventVO;
import com.project.joonggo.domain.GiftconVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventMapper {
    void insertParticipation(EventVO eventVO);

    void insertGiftcon(GiftconVO giftconVO);

    int checkTodayRoulette(Long userId, String roulette);
}
