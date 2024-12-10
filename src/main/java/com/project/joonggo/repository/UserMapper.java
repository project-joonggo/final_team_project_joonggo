package com.project.joonggo.repository;

import com.project.joonggo.domain.UserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int insert(UserVO userVO);
    
    UserVO findUserByIdAndSignFlag(String id, int signflag);

    UserVO findUserByEmail(String userId, int signFlagDefault);

    Long getUsernumByUserId(String userId);
}
