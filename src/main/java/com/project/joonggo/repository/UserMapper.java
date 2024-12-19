package com.project.joonggo.repository;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.domain.AuthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    int insert(UserVO userVO);

    UserVO findUserByIdAndSignFlag(@Param("id") String id, @Param("signflag") int signflag);

    UserVO selectUserNum(long userNum);

    List<AuthVO> selectAuths(long userNum);

    UserVO selectUserByEmailAndDefaultSignFlag(String username);

    int insertAuthInit(long userNum);

    long getMaxUserNum();

//    UserVO findUserByEmail(String userId, int signFlagDefault);

    Long getUsernumByUserId(String userId);

    List<UserVO> getList();

    List<UserVO> findByNameAndEmail(@Param("name") String name, @Param("email") String email);

    void upScore(Long userNum);
    void downScore(Long userNum);

    Long getAdminId();

    void updatePassword(@Param("userId") String userId, @Param("encodedPassword") String encodedPassword);

    void modify(UserVO userVO);

    void delete(long userNum);

    List<Map<String, Object>> searchFraudUsers(String keyword);

    String getUserName(long userNum);
}
