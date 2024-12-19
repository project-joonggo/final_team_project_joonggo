package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QnaFileVO {
    private String uuid;
    private String saveDir;
    private String fileName;
    private int fileType;
    private Long qnaId;
    private Long fileSize;
    private LocalDateTime regAt;
    private String fileUrl;
}
