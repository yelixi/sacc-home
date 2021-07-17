package org.sacc.SaccHome.service;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:47
 **/
public interface EmailService {
    void sendSimpleMail(String to, String verificationCode);
}
