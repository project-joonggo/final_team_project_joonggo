package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVO {
    private Long ano;        // 답변 번호
    private Long qnaId;     // 질문 번호
    private Long userNum;   // 사용자 번호
    private String answer;   // 답변 내용
    private LocalDateTime regAt; // 답변 등록 시간 (LocalDateTime 사용)
}
