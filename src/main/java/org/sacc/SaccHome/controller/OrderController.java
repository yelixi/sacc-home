package org.sacc.SaccHome.controller;


import org.sacc.SaccHome.api.CommonResult;
import org.sacc.SaccHome.enums.RoleEnum;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.Page;
import org.sacc.SaccHome.service.OrderService;
import org.sacc.SaccHome.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RoleUtil roleUtil;

    /**
     * 查询七天内的预约
     * @param currentPage
     * @param token
     * @return
     */
    @GetMapping ("/getOrder")
    @ResponseBody
    public CommonResult<Page<Order>> findNextWeek(@RequestParam int currentPage,@RequestHeader String token){
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)) {
            Page<Order> page = orderService.findNextWeek(currentPage);
            if (page.getTotalNumber()==0) {
                return CommonResult.success(null, "最近七天内无预约");
            } else {
                return CommonResult.success(page);
            }
        }else {
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 新增预约
     * @param order
     * @param token
     * @return
     * @throws ParseException
     */
    @PostMapping("/applyOrder")
    public CommonResult <Order> save(@RequestBody Order order,@RequestHeader String token) throws ParseException {
        if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            if(orderService.judgeTime(order).equals("时间格式正确")) {
                if (orderService.judgeTimeCorrect(order)) {   //判断输入的时间段是否已被预约
                    Timestamp time = Timestamp.valueOf(LocalDateTime.now());
                    order.setCreatedAt(time);
                    order.setUpdatedAt(time);   //设置当前的时间
                    order.setUserId(orderService.getUserIdByToken(token));   //解析token，设置userId
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
                return CommonResult.failed(orderService.judgeTime(order));
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 删除预约
     * @param id
     * @param userId
     * @param token
     * @return
     */
    @DeleteMapping("/deleteOrder")
    public CommonResult deleteByIndex(@RequestParam int id,@RequestParam int userId,@RequestHeader String token){
        if(roleUtil.hasRole(token, RoleEnum.ADMIN)){
            orderService.deleteById(id);
            return CommonResult.success(null,"删除预约成功");
        }else if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int userIdByToken=orderService.getUserIdByToken(token);
            if(userId==userIdByToken){
                orderService.deleteById(id);
                return CommonResult.success(null,"删除预约成功");
            }else{
                return CommonResult.failed("此预约并非该用户创建，无法删除");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }

    /**
     * 更新预约
     * @param order
     * @param token
     * @return
     * @throws ParseException
     */
    @PutMapping("/updateOrder")
    public CommonResult update(@RequestBody Order order,@RequestHeader String token) throws ParseException {
        if(roleUtil.hasRole(token, RoleEnum.ADMIN)){
            orderService.deleteTimeById(order.getId());      //将startTime和endTime设置为null，防止对下面时间的判断产生影响
            if (orderService.judgeTime(order).equals("时间格式正确")) {
                if (orderService.judgeTimeCorrect(order)) {   //判断输入的时间段是否已被预约
                    orderService.update(order);
                    return CommonResult.success(null, "更新预约成功");
                } else {
                    return CommonResult.failed("该时间段已被预约");
                }
            } else {
                return CommonResult.failed(orderService.judgeTime(order));
            }
        }else if(roleUtil.hasRole(token, RoleEnum.MEMBER)){
            int userIdByToken=orderService.getUserIdByToken(token);
            if(order.getUserId()==userIdByToken) {
                orderService.deleteTimeById(order.getId());      //将startTime和endTime设置为null，防止对下面时间的判断产生影响
                if (orderService.judgeTime(order).equals("时间格式正确")) {
                    if (orderService.judgeTimeCorrect(order)) {   //判断输入的时间段是否已被预约
                        orderService.update(order);
                        return CommonResult.success(null, "更新预约成功");
                    } else {
                        return CommonResult.failed("该时间段已被预约");
                    }
                } else {
                    return CommonResult.failed(orderService.judgeTime(order));
                }
            }else{
                return CommonResult.failed("此预约并非该用户创建，无法修改");
            }
        }else{
            return CommonResult.unauthorized(null);
        }
    }
}
