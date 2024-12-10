package com.project.joonggo.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileDTO {
    private BoardVO boardVO;
    private List<FileVO> fileVOList;
}
