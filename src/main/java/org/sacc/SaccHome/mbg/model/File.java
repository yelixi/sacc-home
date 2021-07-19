package org.sacc.SaccHome.mbg.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class File {
    @ApiModelProperty("文件id")
    private Integer id;
    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "属于的文件任务id")
    private Integer fileTaskId;
    @ApiModelProperty(value = "文件路径")
    private String path;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;


}