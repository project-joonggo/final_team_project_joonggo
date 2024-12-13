package com.project.joonggo.service;

import com.project.joonggo.domain.AuthVO;
import com.project.joonggo.domain.UserVO;
import com.project.joonggo.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;

    @Transactional
    @Override
    public int insert(UserVO userVO) {
        int isOk = userMapper.insert(userVO);
        log.info(">>>>>>>>>>>>>>>>>>>>>> userVO : {}", userVO);


        if (isOk > 0) {

            long userNum = userMapper.getMaxUserNum();

            userMapper.insertAuthInit(userNum);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>insertAUthInit할 유저의 pk >>>>> {}", userNum);
        }
        return isOk;
    }

    @Override
    public UserVO findUserByIdAndSignFlag(String id, int signflag) {
        return userMapper.findUserByIdAndSignFlag(id, signflag);
    }

/*    @Override
    public UserVO findUserByEmail(String userId, int signFlagDefault) {
        return userMapper.findUserByEmail(userId, signFlagDefault);
    }*/

    @Override
    public Long getUsernumByUserId(String userId) {
        return userMapper.getUsernumByUserId(userId);
    }

    @Override
    public List<UserVO> getList(){

        List<UserVO> userList = userMapper.getList();
        for(UserVO userVO : userList){
            userVO.setAuthList(userMapper.selectAuths(userVO.getUserNum()));
        }
        log.info(">>>> userList >> {}", userList);
        return userList;
    }

    public UserVO getUserWithAuthorities(String username) {
        UserVO userVO = userMapper.selectUserByEmailAndDefaultSignFlag(username);
        if (userVO != null) {
            // 권한을 명시적으로 조회하여 세팅
            userVO.setAuthList(userMapper.selectAuths(userVO.getUserNum()));
        }
        return userVO;
    }

    @Override
    public List<UserVO> findByNameAndEmail(String name, String email) {
        return userMapper.findByNameAndEmail(name, email);
    }

}
