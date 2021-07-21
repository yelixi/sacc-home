package org.sacc.SaccHome.util;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Email {
    private String to;
    private String subject;
    private String content;

    public Email(){
        this.subject = "欢迎来到SACC————验证码";
        this.content = RandomUtil.randomStringUpper(6);
    }
}
