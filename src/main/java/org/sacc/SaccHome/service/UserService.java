package org.sacc.SaccHome.service;

import org.sacc.SaccHome.mbg.model.User;

import java.util.List;

public interface UserService {


    User getUser(Integer id);

    List<User> getUsersByFileTask(Integer id);

}
