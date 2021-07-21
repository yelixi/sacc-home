package org.sacc.SaccHome.service;

import org.sacc.SaccHome.mbg.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    public List<Order> findNextWeek();

    public Order save(Order order);

    Boolean judgeTimeCorrect(Order order);
}
