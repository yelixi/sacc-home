package org.sacc.SaccHome.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.sacc.SaccHome.pojo.Order;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 显示七天内的预约
     * @return
     */
    List<Order> findNextWeek();

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

}