package com.dm.docmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dm.docmind.commonResponse.CommonResponse;
import com.dm.docmind.entity.User;
import com.dm.docmind.persistence.UserMapper;
import com.dm.docmind.service.UserService;
import com.dm.docmind.tool.HashPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * @author mao19
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HashPassword hashPassword;

    @Override
    public CommonResponse<Object> registerUser(User user) throws NoSuchAlgorithmException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", user.getUserId());
        if (userMapper.exists(queryWrapper)) {
            return CommonResponse.createForError("user already exists");
        }
        String salt = hashPassword.saltGener();
        user.setSalt(salt);
        String hashPwd = hashPassword.SHA256Gener(user.getPassword(), salt);
        user.setPassword(hashPwd);
        userMapper.insert(user);
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Object> loginUser(User user) throws NoSuchAlgorithmException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", user.getUserId());
        if (userMapper.exists(queryWrapper)) {
            User user1 = userMapper.selectOne(queryWrapper);
            String salt = user1.getSalt();
            String inputPassword = user.getPassword();
            String hashPwd = hashPassword.SHA256Gener(inputPassword, salt);
            if (hashPwd.equals(user1.getPassword())) {
                return CommonResponse.createForSuccess();
            } else {
                return CommonResponse.createForError("user password does not match");
            }

        } else {
            return CommonResponse.createForError("user not exists");
        }
    }
}
