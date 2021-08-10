package org.sacc.SaccHome.service.Impl;

import org.sacc.SaccHome.mbg.mapper.UserInfoMapper;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.mbg.model.UserInfo;
import org.sacc.SaccHome.service.UserInfoService;
import org.sacc.SaccHome.service.UserService;
import org.sacc.SaccHome.vo.UserInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by 林夕
 * Date 2021/8/9 19:34
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserService userService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfoVo getThis(String username) {
        User u = userService.getUserInfo(username);
        UserInfoVo userInfoVo = new UserInfoVo(u);
        UserInfo userInfo = userInfoMapper.selectByUserId(u.getId());
        userInfoVo.setUserInfo(userInfo);
        return userInfoVo;
    }

    @Override
    public UserInfoVo getOne(Integer userId) {
        User u = userService.getUser(userId);
        UserInfoVo userInfoVo = new UserInfoVo(u);
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        userInfoVo.setUserInfo(userInfo);
        return userInfoVo;
    }

    @Override
    public boolean updateUserInfo(String username, UserInfo userInfo) {
        User u = userService.getUserInfo(username);
        userInfo.setUserId(u.getId());
        UserInfo userInfo1 = userInfoMapper.selectByUserId(u.getId());
        if(userInfo1!=null)
            return userInfoMapper.updateByPrimaryKeySelective(userInfo)==1;
        else return userInfoMapper.insert(userInfo)==1;
    }

    @Override
    public String uploadAvatar(String username, String url) {
        User u = userService.getUserInfo(username);
        UserInfo userInfo = userInfoMapper.selectByUserId(u.getId());
        if(userInfo==null) {
            UserInfo userInfo1 = new UserInfo();
            userInfo1.setUserId(u.getId());
            userInfo1.setImgUrl(url);
            if (userInfoMapper.updateByPrimaryKeySelective(userInfo1)==1)
                return url;
        }
        assert userInfo != null;
        userInfo.setImgUrl(url);
        if (userInfoMapper.updateByPrimaryKeySelective(userInfo)==1)
            return url;
        return "数据库异常";
    }
}
