package org.sacc.SaccHome.controller;


import org.sacc.SaccHome.pojo.Order;
import org.sacc.SaccHome.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 查找七天内的预约
     * @return
     */
    @RequestMapping ("/getOrder")
    @ResponseBody
    public List<Order> findNextWeek(){
        List<Order> order=orderService.findNextWeek();
        if(CollectionUtils.isEmpty(order)){
            return null;
        }else{
            return order;
        }
    }

    /**
     * 新增预约
     * @return
     */
    @GetMapping("/applyOrder")
    public Order save(Order order){
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        order.setCreated_at(time);
        order.setUpdated_at(time);   //设置当前的时间
        return orderService.save(order);
    }

}
