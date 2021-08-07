package org.sacc.SaccHome.mbg.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.OrderExample;
import org.sacc.SaccHome.mbg.model.PageParam;

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
    List<Order> findNextWeek(PageParam pageParam);

    int getCount();

    /**
     * 新增预约
     * @param order
     * @return
     */
    int save(Order order);


    /**
     * 判断时间段a-b是否与已有的预约时间重复
     * @param order
     * @return
     */
    List<Order> judgeTimeCorrect(Order order);

    Order findIdByIndex(int index);

    void deleteById(int id);

    int update(Order order);

    void deleteTimeById(int id);
}
