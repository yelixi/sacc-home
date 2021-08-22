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

    void updateEmailByUsername(@Param("username") String username, @Param("email") String email);

    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password, @Param("salt") String salt);

    String findPasswordByUsername(@Param("username") String username);

    String findSaltByUsername(@Param("username") String username);


    @Select("select username from user where email=#{email}")
    String selectUsernameByEmail(@Param("email") String email);

    
    @Insert("INSERT INTO user ( username, email, password, salt, judge, created_at, role)"
            + "VALUES(#{username}, #{email}, #{password},#{salt},#{judge},#{createdAt},#{role})")

    int insertUser(User user);

    /**
     * 根据用户名查询用户，因为用户名是唯一索引
     *
     * @param username
     * @return
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND judge=0")
    List<User> selectUserByUserName(@Param("username") String username);

    /**
     * 修改激活
     *
     * @param username
     * @return
     */
    @Update("UPDATE user SET judge = 1 WHERE username = #{username}")
    int updateUserByUserName(@Param("username") String username);

    /**
     * 登录查询
     *
     * @param
     * @return
     */
    @Select("SELECT username, password, salt,role FROM user WHERE username = #{username} AND judge = 1")
    User loginUser(@Param("username") String username);

    /**
     * 密码覆盖
     *
     * @param
     * @return
     */
    @Update("UPDATE user SET password = #{password} WHERE username = #{username}")
    void updatePassword(@Param("username") String username , @Param("password") String password);

    /**
     * 单纯查找
     * @param
     * @return
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    List<User> selectUser(@Param("username") String username);
}
