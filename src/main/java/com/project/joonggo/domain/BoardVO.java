package com.project.joonggo.domain;

import lombok.*;

import java.text.NumberFormat;
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
    private String boardContent;
    private int likeCount;
    private int readCount;
    private LocalDateTime regAt;
    private String isDel;

    // 포맷된 가격을 바로 반환하는 메소드
    public String getFormattedPrice() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        return numberFormat.format(this.tradePrice);  // 가격을 3자리마다 쉼표 찍어서 반환
    }
}