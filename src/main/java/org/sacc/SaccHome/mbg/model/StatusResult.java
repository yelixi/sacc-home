package org.sacc.SaccHome.mbg.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatusResult {

    @ApiModelProperty(value = "剩余时间")
    private String exp_left;
    @ApiModelProperty(value = "提交的用户数")
    private Integer numsCommitted;

    @ApiModelProperty(value = "文件任务名称")
    private String taskName;
    @ApiModelProperty(value = "文件任务id")
    private Integer taskId;


}
