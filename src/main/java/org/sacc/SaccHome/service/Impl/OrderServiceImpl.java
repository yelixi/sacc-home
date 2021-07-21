package org.sacc.SaccHome.service.Impl;


import org.sacc.SaccHome.mbg.mapper.OrderMapper;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        if(save==1){
            return order;
        }else{
            return null;
        }
    }

    @Override
    public Boolean judgeTimeCorrect(Order order) {
        List<Order> flag=ordermapper.judgeTimeCorrect(order);
        if(CollectionUtils.isEmpty(flag)){
            return true;
        }else{
            return false;
        }
    }
}
