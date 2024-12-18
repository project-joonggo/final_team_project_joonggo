package com.project.joonggo.domain;

import lombok.*;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QnaFileDTO {
   private QnaVO qnaVO;
   private List<QnaFileVO> qnaFileVOList;
}
