package org.sacc.SaccHome.mbg.mapper;

import org.apache.ibatis.annotations.Select;
import org.sacc.SaccHome.mbg.model.Activity;

import java.util.List;

public interface ActivityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Activity record);

    int insertSelective(Activity record);

    Activity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Activity record);

    int updateByPrimaryKey(Activity record);

    @Select("select * from activity")
    List<Activity> selectAll();
}
