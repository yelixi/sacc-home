package org.sacc.SaccHome.mbg.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.sacc.SaccHome.mbg.model.User;

import java.util.List;

@Mapper
public interface UserDao {
    List<User> getUsersByFileTask(Integer id);
}
