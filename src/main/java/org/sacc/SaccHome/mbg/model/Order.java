package org.sacc.SaccHome.mbg.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private Integer id;

    private String startTime;

    private String endTime;
    //课堂名称
    private String name;
    //课堂主讲人
    private String user;
    //课堂介绍
    private String introduction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updatedAt;

}
