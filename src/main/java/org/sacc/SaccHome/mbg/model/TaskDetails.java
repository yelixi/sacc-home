package org.sacc.SaccHome.mbg.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author weiwo
 * @Created on 2021/8/24
 */
@Data
@NoArgsConstructor
public class TaskDetails {
    private List<File> files;

    private  List<UserParam> userInfos;
}
