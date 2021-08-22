package org.sacc.SaccHome.mbg.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StatusResult {

    @ApiModelProperty(value = "剩余时间")
     private String exp_left;

    @ApiModelProperty(value = "提交的用户数")
    private Integer numsCommitted;

    @ApiModelProperty(value = "已提交的用户列表")
    private List<UserParam>  committedUsers;
    @ApiModelProperty(value = "创建该文件任务的用户id")
    private Integer id;

}
