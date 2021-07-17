package org.sacc.SaccHome.service.Impl;

import org.sacc.SaccHome.enums.ResultCode;
import org.sacc.SaccHome.exception.AuthenticationException;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.service.EmailService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:15
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String getEmailByUsername(String username) {
        return userMapper.getEmailByUsername(username);
    }

    @Override
    public void updateEmailByUsername(String username, String email) {
        //如果redis中有对应的key，表示用户没有通过刚才的验证（防止用户直接通过URL修改）
        if (redisTemplate.hasKey(username)) {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        } else {
            userMapper.updateEmailByUsername(username, email);
        }
    }

    @Override
    public void updatePasswordByUsername(String username, String oldPassword, String newPassword) {
        //todo 等写登录和注册的xdm确定了密码加密方式后修改，目前仅作明文相等判断
        if (userMapper.findPasswordByUsername(username) == oldPassword) {
            userMapper.updatePasswordByUsername(username, newPassword);
        } else {
            throw new AuthenticationException(ResultCode.WRONG_PASSWORD);
        }
    }

    @Override
    public void forgetPassword(String username, String password) {
        //如果redis中有对应的key，表示用户没有通过刚才的验证（防止用户直接通过URL修改）
        if (redisTemplate.hasKey(username)) {
            throw new AuthenticationException(ResultCode.FAILED_VERIFICATION);
        } else {
            userMapper.updatePasswordByUsername(username, password);
        }
    }

    @Override
    public boolean judgeVerificationCode(String username, String inputVerificationCode) {
        String LocalVerificationCode = (String) redisTemplate.opsForValue().get(username);
        if (LocalVerificationCode == inputVerificationCode) {
            //删除相应的key并返回true
            redisTemplate.delete(username);
            return true;
        }
        return false;
    }

    @Override
    public void sendVerificationCodeEmail(String username) {
        String email = this.getEmailByUsername(username);
        String verificationCode = VerificationCodeGenerator.getVerificationCode(6);
        redisTemplate.opsForValue().setIfAbsent(username, verificationCode, 1, TimeUnit.HOURS);
        emailService.sendSimpleMail(email, verificationCode);
    }
}
