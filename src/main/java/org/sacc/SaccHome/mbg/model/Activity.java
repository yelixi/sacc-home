package org.sacc.SaccHome.mbg.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Activity {
    private Integer id;

    private String activityName;

    private String fileUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer userId;
}
