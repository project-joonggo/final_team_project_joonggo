package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QnaVO {
    private Long qnaId;        // qna_id
    private Long userId;       // user_id
    private String category;   // category
    private String qnaName;    // qna_name
    private String qnaContent; // qna_content
    private LocalDateTime regAt;        // reg_at
    private String isDel;      // is_del
    private int answerCount;

    private String formattedRegAt; //regAt을 화면에서 보기좋게
}
