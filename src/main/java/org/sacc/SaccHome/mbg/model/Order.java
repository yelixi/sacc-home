package org.sacc.SaccHome.mbg.model;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private Integer id;

    private Date startTime;

    private Date endTime;

    private Integer number;

    private String nameList;

    private Integer userId;

    private Date createdAt;

    private Date updatedAt;

}
