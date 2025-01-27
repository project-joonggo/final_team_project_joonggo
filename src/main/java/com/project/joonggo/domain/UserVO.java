package com.project.joonggo.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
    private String address1;
    private String address2;
    private String address3;
    private String postCode;
    private int signflag;
    private String socialId;
    private String phone;
    private String regDate;

    public List<AuthVO> authList = new ArrayList<>();

}
