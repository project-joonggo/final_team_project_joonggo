package com.project.joonggo.service;

import com.project.joonggo.domain.PagingVO;
import com.project.joonggo.domain.UserVO;

import java.util.List;
import java.util.Map;

public interface LoginService {

    int insert(UserVO userVO);

    UserVO findUserByIdAndSignFlag(String id, int signflag);

    /*UserVO findUserByEmail(String userId, int signFlagDefault);*/

    Long getUsernumByUserId(String userId);

    List<UserVO> getList();

    UserVO getUserWithAuthorities(String username);

    List<UserVO> findByNameAndEmail(String name, String email);

    void updatePassword(String userId, String encodedPassword);

    void modify(UserVO userVO);

    void delete(long userNum);

    List<Map<String, Object>> searchFraudUsers(String keyword);

    Long getAdminId();

    String getUserName(long userNum);

    int getTotal(PagingVO pgvo);

    List<UserVO> getUserList(PagingVO pgvo);

    void banUser(long userNum);

    String getSellerAddressByUserNum(long sellerId);
}
