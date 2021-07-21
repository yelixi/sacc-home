package org.sacc.SaccHome.controller;


import org.sacc.SaccHome.api.CommonResult;
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
     * 查询七天内的预约
     * @return
     */
    @RequestMapping ("/getOrder")
    @ResponseBody
    public CommonResult<List<Order>> findNextWeek(){
        List<Order> order=orderService.findNextWeek();
        if(CollectionUtils.isEmpty(order)){
            return CommonResult.success(null,"最近七天内无预约");
        }else{
            return CommonResult.success(order);
        }
    }

    /**
     * 新增预约
     * @return
     */
    @GetMapping("/applyOrder")
    public CommonResult <Order> save(Order order){
        if(orderService.judgeTimeCorrect(order)){   //判断输入的时间段是否正确
            Timestamp time = Timestamp.valueOf(LocalDateTime.now());
            order.setCreated_at(time);
            order.setUpdated_at(time);   //设置当前的时间
            Order save=orderService.save(order);
            if(save!=null){
                return CommonResult.success(save,"新增预约成功");
            }else{
                return CommonResult.failed("新增预约失败");
            }
        }else{
            return CommonResult.failed("该时间段已被预约");
        }
    }

}
