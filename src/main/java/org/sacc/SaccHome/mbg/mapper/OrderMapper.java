package org.sacc.SaccHome.mbg.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.OrderExample;

@Mapper
public interface OrderMapper {
    long countByExample(OrderExample example);

    int deleteByExample(OrderExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectByExample(OrderExample example);

    Order selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * 显示七天内的预约
     * @return
     */
    List<org.sacc.SaccHome.pojo.Order> findNextWeek();

    /**
     * 新增预约
     * @param order
     * @return
     */
    int save(org.sacc.SaccHome.pojo.Order order);
}
