package com.project.joonggo.security;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.repository.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
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

    }
}
