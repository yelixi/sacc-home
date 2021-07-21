package org.sacc.SaccHome.mbg.model;

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class User {
    private Integer id; //主键
    private String username;//学号，即用户名
    private String email;//邮箱
    private String password;//输入的密码
    private String salt;//用于加密的盐
    private String role;//身份，默认学生，判断权限用
    private LocalDateTime createAt;//创建时间
    private Byte judge;//判断是否验证

    public void setSalt(){
        this.salt = RandomUtil.randomString(6);
    }


}
