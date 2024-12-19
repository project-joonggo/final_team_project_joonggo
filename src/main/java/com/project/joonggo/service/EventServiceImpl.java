package com.project.joonggo.service;

import com.project.joonggo.domain.EventVO;
import com.project.joonggo.domain.GiftconVO;
import com.project.joonggo.repository.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
