package com.project.joonggo.repository;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.domain.AuthVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(UserVO userVO);
    
    UserVO findUserByIdAndSignFlag(String id, int signflag);

    UserVO selectUserNum(long userNum);

    List<AuthVO> selectAuths(long userNum);

    UserVO selectUserByEmailAndDefaultSignFlag(String username);

    int insertAuthInit(long userNum);

    long getMaxUserNum();
}
