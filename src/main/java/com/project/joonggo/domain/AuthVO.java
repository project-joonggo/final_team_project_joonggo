package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthVO {
    private long authId;
    private long userNum;
    private String auth;

}
