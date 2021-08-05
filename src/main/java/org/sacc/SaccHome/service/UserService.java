package org.sacc.SaccHome.service;

import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.util.Email;

import java.util.List;

public interface UserService {


    User getUser(Integer id);

    List<User> getUsersByFileTask(Integer id);

    String getEmailByUsername(String username);

    void updateEmailByUsername(String username, String email);

    void updatePasswordByUsername(String username, String oldPassword, String newPassword);

    void forgetPassword(String username,String password);

    boolean judgeVerificationCode(String username, String inputVerificationCode);

    void sendVerificationCodeEmail(String username);

    User getUserInfo(String username);

    CommonResult createAccount(User user, Email email);

    CommonResult loginAccount(User user);

    CommonResult verifyAccount(String username,String code, String inCode);

    CommonResult registerAll(String address);

    boolean authorize(Integer userId,String role);
}
