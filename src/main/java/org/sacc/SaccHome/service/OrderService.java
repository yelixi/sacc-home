package org.sacc.SaccHome.service;

import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.Page;
import org.springframework.stereotype.Service;


@Service
public interface OrderService {

    public Page<Order> findNextWeek(int currentPage);

    public Order save(Order order);

    Boolean judgeTimeCorrect(Order order);

    int findIdByIndex(int index);

    void deleteById(int id);

    void update(Order order);

    void deleteTimeById(int id);
}
