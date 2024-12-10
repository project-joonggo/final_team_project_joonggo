package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private String uuid;
    private String saveDir;
    private String fileName;
    private int fileType;
    private long boardId;
    private long fileSize;
    private LocalDateTime regAt;
    private String fileUrl;
}
