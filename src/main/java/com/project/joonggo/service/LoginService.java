package com.project.joonggo.service;

import com.project.joonggo.domain.UserVO;

import java.util.List;

public interface LoginService {

    int insert(UserVO userVO);

    UserVO findUserByIdAndSignFlag(String id, int signflag);

    /*UserVO findUserByEmail(String userId, int signFlagDefault);*/

    Long getUsernumByUserId(String userId);

    List<UserVO> getList();

    UserVO getUserWithAuthorities(String username);

    List<UserVO> findByNameAndEmail(String name, String email);

}
