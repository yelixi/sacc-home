package org.sacc.SaccHome.controller;


import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.service.OrderService;
import org.sacc.SaccHome.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RoleUtil roleUtil;

    /**
     * 查询七天内的预约
     * @return
     */
    @GetMapping ("/getOrder")
    @ResponseBody
    public CommonResult<List<Order>> findNextWeek(@RequestHeader String token){
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)) {
            List<Order> order = orderService.findNextWeek();
            if (CollectionUtils.isEmpty(order)) {
                return CommonResult.success(null, "最近七天内无预约");
            } else {
                return CommonResult.success(order);
            }
        }else {
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 新增预约
     * @return
     */
    @GetMapping("/applyOrder")
    public CommonResult <Order> save(Order order,@RequestHeader String token) throws ParseException{
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            String statrTime=order.getStartTime();
            String endTime=order.getEndTime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sf.parse(statrTime);
            Date date2 = sf.parse(endTime);
            if(date1.before(date2)){         //判断起始时间是否早于结束时间
                if(date1.after(new Date())) {       //判断起始时间是否比当前时间晚
                    if (orderService.judgeTimeCorrect(order)) {   //判断输入的时间段是否正确
                        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
                        order.setCreatedAt(time);
                        order.setUpdatedAt(time);   //设置当前的时间
                        Order save = orderService.save(order);
                        if (save != null) {
                            return CommonResult.success(save, "新增预约成功");
                        } else {
                            return CommonResult.failed("新增预约失败");
                        }
                    } else {
                        return CommonResult.failed("该时间段已被预约");
                    }
                }else{
                    return CommonResult.failed("请输入今日之后的时间段");
                }
            }else{
                return CommonResult.failed("起始时间需早于结束时间");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 删除预约
     * @param index
     * @param token
     * @return
     */
    @DeleteMapping("/deleteOrder")
    public CommonResult deleteByIndex(int index,@RequestHeader String token){
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int id=orderService.findIdByIndex(index);
            orderService.deleteById(id);
            return CommonResult.success(null);
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 更新预约
     * @param order
     * @param index
     * @param token
     * @return
     * @throws ParseException
     */
    @PutMapping("/updateOrder")
    public CommonResult update(Order order,int index,@RequestHeader String token) throws ParseException {
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int id=orderService.findIdByIndex(index);
            order.setId(id);    //设置order的id，根据这个去更新
            orderService.deleteTimeById(id);      //将startTime和endTime设置为null，防止对下面时间的判断产生影响
            String statrTime=order.getStartTime();
            String endTime=order.getEndTime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sf.parse(statrTime);
            Date date2 = sf.parse(endTime);
            if(date1.before(date2)){         //判断起始时间是否早于结束时间
                if(date1.after(new Date())) {       //判B断起始时间是否比当前时间晚
                    if (orderService.judgeTimeCorrect(order)) {   //判断输入的时间段是否正确
                        orderService.update(order);
                        return CommonResult.success(null);
                    } else {
                        return CommonResult.failed("该时间段已被预约");
                    }
                }else{
                    return CommonResult.failed("请输入今日之后的时间段");
                }
            }else{
                return CommonResult.failed("起始时间需早于结束时间");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }
}
