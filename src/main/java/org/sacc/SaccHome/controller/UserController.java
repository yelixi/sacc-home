package org.sacc.SaccHome.controller;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.EmailService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.util.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:13
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    private EmailService emailService;
    @Resource
    private RedisTemplate redisTemplate;

    @PutMapping("/sendEmail")
    public void sendEmail(String username) {
        userService.sendVerificationCodeEmail(username);
    }

    @PostMapping("/judgeCode")
    public boolean judgeCode(String username, String code) {
        return userService.judgeVerificationCode(username, code);
    }

    @PutMapping("/updateEmail")
    public void updateEmail(String email, String username) {
        userService.updateEmailByUsername(email, username);
    }

    @PostMapping("/forgetPassword")
    public void forgetPassword(String username, String password) {
        userService.forgetPassword(username, password);
    }

    @PutMapping("/updatePassword")
    public void updatePassword(String username, String oldPassword, String newPassword){
        userService.updatePasswordByUsername(username,oldPassword,newPassword);
    }

    /**
     * 注册
     * @param username
     * @param password
     * @param email
     * @return CommonResult
     */
    @PostMapping("register")
    public CommonResult createAccount(String username, String password, String email){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setSalt();
        user.setEmail(email);

        Email e = new Email();
        e.setTo(email);
        emailService.sendEmail(e);
        redisTemplate.opsForValue().set(username,e.getContent(),1, TimeUnit.DAYS);
        return userService.createAccount(user);
    }


    /**
     * 验证码检验
     * @param username
     * @param inCode
     * @return
     */
    @PostMapping("verification")
    public CommonResult verifyAccount(String username, String inCode){
        String code = (String) redisTemplate.opsForValue().get(username);
        return userService.verifyAccount(username,code,inCode);
    }

    /**
     * 成功
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public CommonResult loginAccount(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userService.loginAccount(user);
    }

    @PostMapping("registerByTeam")
    public CommonResult teamRegister(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userService.teamRegister(user);
    }
}