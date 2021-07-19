package org.sacc.SaccHome.controller;

import org.sacc.SaccHome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:13
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

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
}
