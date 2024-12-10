package com.project.joonggo.security;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserService implements UserDetailsService {

    @Autowired
    public UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        UserVO userVO = userMapper.selectUserByEmailAndDefaultSignFlag(username);

        if (userVO == null) {
            throw new UsernameNotFoundException("CustomDetailService >>> User not found");
        }

        System.out.println("로그인 시도: " + username);
        System.out.println("DB에서 조회된 유저: " + userVO);

        userVO.setAuthList(userMapper.selectAuths(userVO.getUserNum()));

        return new AuthUser(userVO);



/*        long userNum;

        try {
            // username을 long 타입 userNum으로 변환
            userNum = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid username format. Must be numeric.");
        }


        UserVO userVO = userMapper.selectUserNum(userNum);
        userVO.setAuthList(userMapper.selectAuths(userNum));
        //UserDetails return
        return new AuthUser(userVO);*/
    }
}
