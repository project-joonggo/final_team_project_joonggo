package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportVO {
    private long reportNum;
    private long reportCompId;
    private long userNum;
    private long boardId;
    private String status;
    private String reportDate;

    private String compContent; // 신고 사유 필드 추가
}
