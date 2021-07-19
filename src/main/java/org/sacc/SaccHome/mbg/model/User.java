package org.sacc.SaccHome.mbg.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class User {
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "身份信息")
    private String role;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;


}