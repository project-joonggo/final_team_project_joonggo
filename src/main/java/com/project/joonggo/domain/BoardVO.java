package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardVO {
    private long boardId;
    private long sellerId;
    private String category;
    private String boardName;
    private int tradeFlag;
    private int tradePrice;
    private String content;
    private int likeCount;
    private int readCount;
    private LocalDateTime regAt;
    private String isDel;
    private String merchantUid;
}