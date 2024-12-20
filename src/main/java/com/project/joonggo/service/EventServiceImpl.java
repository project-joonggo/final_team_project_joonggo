package com.project.joonggo.service;

import com.project.joonggo.domain.EventVO;
import com.project.joonggo.domain.GiftconVO;
import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.repository.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public void processRouletteResult(Long userId, String result) {
        EventVO eventVO = new EventVO(userId, "roulette");
        eventMapper.insertParticipation(eventVO);

        if (!"꽝".equals(result)) {
            // 당첨이면 기프티콘 발행
            GiftconVO giftconVO = new GiftconVO();
            giftconVO.setUserNum(userId);
            eventMapper.insertGiftcon(giftconVO);
        }
    }

    @Override
    public boolean checkRouletteToday(Long userId) {
        return eventMapper.checkTodayRoulette(userId) > 0;
    }

    @Override
    public boolean checkAttendToday(Long userId) {
        return eventMapper.checkTodayAttend(userId) > 0;
    }

    @Override
    public boolean processAttendResult(Long userId, String result) {
        EventVO eventVO = new EventVO(userId, "attend");
        eventMapper.insertParticipation(eventVO);

        // 5번째 출석인지 확인
        int attendanceCount = eventMapper.getAttendCount(userId); // 사용자의 출석 횟수 조회
        if (attendanceCount % 5 == 0) { // 5번째 출석이면 기프티콘 발행
            GiftconVO giftconVO = new GiftconVO();
            giftconVO.setUserNum(userId);
            eventMapper.insertGiftcon(giftconVO);
            return true; // 기프티콘 발행했으므로 true 반환
        }

        return false; // 기프티콘 발행하지 않음
    }

    @Override
    public int getAttendCount(Long userId) {
        return eventMapper.getAttendCount(userId);
    }

    @Override
    public int getMyGiftconTotal(PagingVO pgvo, long userId) {
        return eventMapper.getMyGiftconTotal(pgvo, userId);
    }

    @Override
    public List<GiftconVO> getMyGiftconList(PagingVO pgvo, long userId) {
        return eventMapper.getMyGiftconList(pgvo, userId);
    }
}
