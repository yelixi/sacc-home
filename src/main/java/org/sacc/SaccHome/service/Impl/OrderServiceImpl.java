package org.sacc.SaccHome.service.Impl;


import org.sacc.SaccHome.pojo.Order;
import org.sacc.SaccHome.mapper.OrderMapper;
import org.sacc.SaccHome.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper ordermapper;

    @Override
    public List<Order> findNextWeek() {
        return ordermapper.findNextWeek();
    }

    @Override
    public Order save(Order order){
        int save= ordermapper.save(order);
        return order;
    }
}