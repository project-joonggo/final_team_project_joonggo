package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    private long userNum;
    private String userId;
    private String password;
    private String userName;
    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private double score;
    private String isDel;
}
