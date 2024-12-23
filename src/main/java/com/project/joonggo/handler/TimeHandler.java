package com.project.joonggo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TimeHandler {

    // 주어진 등록 시간(regAt)을 현재 시간과 비교해서 경과 시간을 반환
    public static String getTimeAgo(String regAt) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // regAt을 LocalDateTime으로 변환 (예: "2024-12-11T12:20:21" 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime regAtTime = LocalDateTime.parse(regAt, formatter);

        // 두 시간 사이의 차이 계산
        Duration duration = Duration.between(regAtTime, now);

        // 경과 시간에 따른 조건 분기
        long days = duration.toDays();   // 경과 일수
        long hours = duration.toHours(); // 경과 시간
        long minutes = duration.toMinutes(); // 경과 분

        // 경과 시간이 1일 이상이면 "x일 전"
        if (days > 0) {
            return days + "일 전";
        }
        // 24시간 이상 경과했으면 "x시간 전"
        else if (hours > 0) {
            return hours + "시간 전";
        }
        // 1시간 이상, 24시간 이내일 경우 "x분 전"
        else if (minutes > 0) {
            return minutes + "분 전";
        }
        // 1분 미만이면 "방금 전"
        else {
            return "방금 전";
        }
    }
}
