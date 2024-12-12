package com.project.joonggo.domain;

import lombok.*;

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
}
