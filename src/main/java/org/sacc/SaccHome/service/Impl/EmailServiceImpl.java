package org.sacc.SaccHome.service.Impl;

import org.sacc.SaccHome.enums.ResultCode;
import org.sacc.SaccHome.exception.BusinessException;
import org.sacc.SaccHome.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:48
 **/
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendSimpleMail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("院科协SACC平台");
        message.setText("尊敬的SACC平台用户:" + "\n\n" +
                "您好！" + "\n\n" +
                "您的验证码为:" + verificationCode + "。" + "\n" +
                "有效时间为1小时");
        try {
            mailSender.send(message);
        } catch (Exception e){
            throw new BusinessException(ResultCode.EMAIL_SENDING_ABNORMAL);
        }

    }
}
