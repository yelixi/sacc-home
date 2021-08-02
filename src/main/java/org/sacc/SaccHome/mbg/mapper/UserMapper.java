package org.sacc.SaccHome.mbg.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.mbg.model.UserExample;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    String getEmailByUsername(@Param("username") String username);

    void updateEmailByUsername(@Param("username") String username,@Param("email")String email);

    void updatePasswordByUsername(@Param("username") String username,@Param("password")String password,@Param("salt")String salt);

    String findPasswordByUsername(@Param("username") String username);

    String findSaltByUsername(@Param("username")String username);

    @Insert("INSERT INTO user ( username, email, password, salt,judge, created_at)"
            +"VALUES(#{username}, #{email}, #{password},#{salt},#{judge},#{createdAt})")
    int insertUser(User user);

    /**
     * 根据用户名查询用户，因为用户名是唯一索引
     * @param username
     * @return
     */
    @Select("SELECT username,password,email,role, judge FROM user WHERE judge=0")
    List<User> selectUserByUserName(@Param("username") String username);

    /**
     * 修改激活
     * @param username
     * @return
     */
    @Update("UPDATE user SET judge = 1 WHERE username = #{username}")
    int updateUserByUserName(@Param("username") String username);

    /**
     * 登录查询
     * @param
     * @return
     */
    @Select("SELECT username, password, salt,role FROM user WHERE username = #{username} AND judge = 1")
    User loginUser(@Param("username") String username);

    @Insert("INSERT INTO user ( username,password,created_at)"
            +"VALUES(#{username}, #{password}, #{createdAt})")
    int teamInsertUser(User user);
}
