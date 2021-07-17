package org.sacc.SaccHome.mbg.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 02:19
 **/
@Repository
public interface UserMapper {
    String getEmailByUsername(@Param("username") String username);
    void updateEmailByUsername(@Param("username") String username,@Param("email")String email);
    void updatePasswordByUsername(@Param("username") String username,@Param("password")String password);
    String findPasswordByUsername(@Param("username") String username);
}
