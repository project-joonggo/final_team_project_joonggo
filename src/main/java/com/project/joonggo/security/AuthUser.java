package com.project.joonggo.security;

import com.project.joonggo.domain.UserVO;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class AuthUser extends User {

    private UserVO userVO;

    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, UserVO userVO) {
        super(username, password, authorities);
        this.userVO = userVO;
    }

    public AuthUser(UserVO userVO) {
        super(String.valueOf(userVO.getUserNum()), userVO.getPassword(),
                userVO.getAuthList().stream()
                        .map(authVO -> new SimpleGrantedAuthority(authVO.getAuth()))
                        .collect(Collectors.toList())
        );
        this.userVO = userVO;
    }
}
