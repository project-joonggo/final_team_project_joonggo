package com.project.joonggo.repository;

import com.project.joonggo.domain.EventVO;
import com.project.joonggo.domain.GiftconVO;
import com.project.joonggo.domain.PagingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {
    void insertParticipation(EventVO eventVO);

    void insertGiftcon(GiftconVO giftconVO);

    int checkTodayRoulette(Long userId);

    int checkTodayAttend(Long userId);

    int getAttendCount(Long userId);

    int getMyGiftconTotal(PagingVO pgvo, long userId);

    List<GiftconVO> getMyGiftconList(@Param("pgvo") PagingVO pgvo,
                                     @Param("userId") long userId);
}
