package com.project.joonggo.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WishVO {
    private Long wishId;
    private Long userNum;
    private Long boardId;
    private LocalDateTime regAt;
}
