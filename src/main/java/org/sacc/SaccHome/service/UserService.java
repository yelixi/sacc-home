package org.sacc.SaccHome.service;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.util.Email;

import java.util.List;

public interface UserService {


    User getUser(Integer id);

    List<User> getUsersByFileTask(Integer id);

    String getEmailByUsername(String username);

    CommonResult updateEmailByUsername(String username, String email);

    CommonResult updatePasswordByUsername(String username, String oldPassword, String newPassword);

    CommonResult forgetPassword(String username,String password);

    CommonResult judgeVerificationCode(String username, String inputVerificationCode);

    CommonResult sendVerificationCodeEmailByUsername(String username);

    CommonResult sendVerificationCodeEmailByEmail(String email);

    User getUserInfo(String username);

    CommonResult createAccount(User user, Email email);

    CommonResult loginAccount(User user);

    CommonResult verifyAccount(String username,String code, String inCode);

    CommonResult registerAll(String address);

    boolean authorize(String username,String role);

    List<User> getAllUser(String token);
}
