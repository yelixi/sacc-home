package org.sacc.SaccHome.service;

import org.sacc.SaccHome.mbg.model.UserInfo;
import org.sacc.SaccHome.vo.UserInfoVo;

/**
 * Created by 林夕
 * Date 2021/8/9 19:34
 */
public interface UserInfoService {

    UserInfoVo getThis(String username);

    UserInfoVo getOne(Integer userId);

    boolean updateUserInfo(String username, UserInfo userInfo);

    String uploadAvatar(String username,String url);
}
