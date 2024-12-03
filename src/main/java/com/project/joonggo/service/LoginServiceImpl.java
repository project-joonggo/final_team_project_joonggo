package com.project.joonggo.service;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService{
    private final UserMapper userMapper;

    @Transactional
    @Override
    public int insert(UserVO userVO) {
        int isOk = userMapper.insert(userVO);
/*        if(isOk > 0){
            userMapper.insertAuthInit(userVO.getEmail());
        }*/
        return isOk;
    }

    @Override
    public UserVO findUserById(String userId) {
        return userMapper.selectUserById(userId);
    }
}
