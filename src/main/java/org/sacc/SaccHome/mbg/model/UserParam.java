package org.sacc.SaccHome.mbg.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserParam {
    @ApiModelProperty("用户id")
    private  Integer id;

    @ApiModelProperty("用户名")
     private  String useName;
}
