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

}