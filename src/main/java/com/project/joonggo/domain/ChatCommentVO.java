package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatCommentVO {
    private int commentId;
    private int roomId;
    private long commentUserNum;
    private String commentContent;
    private String commentWriteDate;
    private int isRead;
}
