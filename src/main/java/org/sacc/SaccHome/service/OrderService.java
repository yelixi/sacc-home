package org.sacc.SaccHome.service;

import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;


@Service
public interface OrderService {

    Page<Order> findNextWeek(int currentPage);

    Order save(Order order);

    Boolean judgeTimeCorrect(Order order);

    String judgeTime(Order order) throws ParseException;

    void deleteById(int id);

    void update(Order order);

    void deleteTimeById(int id);

    int getUserIdByToken(String token);
}
