package com.project.joonggo.service;

import com.project.joonggo.domain.UserVO;

public interface LoginService {

    int insert(UserVO userVO);

    UserVO findUserByIdAndSignFlag(String id, int signflag);

}
