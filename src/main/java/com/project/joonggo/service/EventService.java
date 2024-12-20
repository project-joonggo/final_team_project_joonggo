package com.project.joonggo.service;

import com.project.joonggo.domain.GiftconVO;
import com.project.joonggo.domain.PagingVO;

import java.util.List;

public interface EventService {
    void processRouletteResult(Long userId, String result);

    boolean checkRouletteToday(Long userId);

    boolean checkAttendToday(Long userId);

    boolean processAttendResult(Long userId, String result);

    int getAttendCount(Long userId);

    int getMyGiftconTotal(PagingVO pgvo, long userId);

    List<GiftconVO> getMyGiftconList(PagingVO pgvo, long userId);
}
