package org.sacc.SaccHome.service.impl;


import org.sacc.SaccHome.mbg.mapper.UserDao;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDao userDao;
    @Override
    public User getUser(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public List<User> getUsersByFileTask(Integer id) {
        List<User> users = userDao.getUsersByFileTask(id);
        return users;
    }
}
