package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {
    private long replyId;      // 대댓글 번호
    private long ano;    // 부모 답변 번호
    private long userNum;      // 대댓글 작성자 번호
    private String reply;      // 대댓글 내용
    private String writerName;
    private LocalDateTime regAt; // 대댓글 등록 시간 (LocalDateTime 사용)
}
