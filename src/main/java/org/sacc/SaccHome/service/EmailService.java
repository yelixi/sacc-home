package org.sacc.SaccHome.service;

import org.sacc.SaccHome.util.Email;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:47
 **/
public interface EmailService {
    void sendSimpleMail(String to, String verificationCode);

    void sendEmail(Email email);
}
