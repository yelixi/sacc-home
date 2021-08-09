package org.sacc.SaccHome.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.mbg.model.UserInfo;
import org.springframework.beans.BeanUtils;

/**
 * Created by 林夕
 * Date 2021/8/9 19:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoVo extends User {

    private UserInfo userInfo;

    public UserInfoVo(User u){
        BeanUtils.copyProperties(u,this);
    }
}
