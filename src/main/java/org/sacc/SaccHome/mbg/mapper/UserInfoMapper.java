package org.sacc.SaccHome.mbg.mapper;

import org.sacc.SaccHome.mbg.model.UserInfo;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    UserInfo selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
}
