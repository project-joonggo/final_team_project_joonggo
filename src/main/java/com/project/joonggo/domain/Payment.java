package com.project.joonggo.domain;

import lombok.*;

import java.text.NumberFormat;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payment {
    private Long paymentId;
    private String merchantUid;
    private Long boardId;
    private int amount;
    private String productName;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private int refundedAmount;
    private boolean cancelFlag;
    private String impUid;
    private Long userNum;


    // 포맷된 가격을 바로 반환하는 메소드
    public String getFormattedAmount() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        return numberFormat.format(this.amount);  // 가격을 3자리마다 쉼표 찍어서 반환
    }

}
