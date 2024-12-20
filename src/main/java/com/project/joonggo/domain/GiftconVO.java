package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GiftconVO {
    private long giftconId;
    private String giftconName;
    private long userNum;
    private String regAt;
    private String endDate;
}


