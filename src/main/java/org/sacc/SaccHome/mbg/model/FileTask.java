package org.sacc.SaccHome.mbg.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileTask {

    @ApiModelProperty(value = "文件任务id")
    private Integer id;
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "发起人")
    private Integer userId;
    @ApiModelProperty(value = "命名规则")
    private String rule;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;
    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;



}