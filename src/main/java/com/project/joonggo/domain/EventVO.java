package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventVO {
    private long eventId;
    private long userNum;
    private String type;
    private String regDay;
    private String regTime;

    public EventVO(Long userId, String roulette) {
        this.userNum = userId;
        this.type = roulette;
    }
}
