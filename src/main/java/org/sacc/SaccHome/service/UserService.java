package org.sacc.SaccHome.service;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:15
 **/
public interface UserService {
    String getEmailByUsername(String username);

    void updateEmailByUsername(String username, String email);

    void updatePasswordByUsername(String username, String oldPassword, String newPassword);

    void forgetPassword(String username,String password);

    boolean judgeVerificationCode(String username, String inputVerificationCode);

    void sendVerificationCodeEmail(String username);
}
