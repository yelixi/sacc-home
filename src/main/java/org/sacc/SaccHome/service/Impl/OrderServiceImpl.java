package org.sacc.SaccHome.service.Impl;



import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.mbg.mapper.OrderMapper;
import org.sacc.SaccHome.mbg.mapper.UserMapper;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.Page;
import org.sacc.SaccHome.mbg.model.PageParam;
import org.sacc.SaccHome.mbg.model.User;
import org.sacc.SaccHome.service.OrderService;
import org.sacc.SaccHome.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper ordermapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtToken jwtToken;

    @Override
    public Page<Order> find(int currentPage) {
        PageParam pageParam = new PageParam();
        Page<Order> page = new Page<Order>();
        pageParam.setCurrentPage(currentPage);
        List<Order> orders = ordermapper.find(pageParam);
        for (Order order : orders) {      //将返回的order中的时间的秒数删除
            order.setStartTime(order.getStartTime().substring(0,16));
            order.setEndTime(order.getEndTime().substring(0,16));
        }
        page.setList(orders);
        page.setCurrentPage(currentPage);
        page.setTotalNumber(ordermapper.getCount());
        if(ordermapper.getCount() % pageParam.getPageSize()==0){
            page.setTotalPage(ordermapper.getCount() /pageParam.getPageSize());
        }else{
            page.setTotalPage(ordermapper.getCount() /pageParam.getPageSize() + 1);
        }
        return page;
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

    @Override
    public String judgeTime(Order order) throws ParseException {
        String statrTime=order.getStartTime();
        String endTime=order.getEndTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = sf.parse(statrTime);
        Date date2 = sf.parse(endTime);
        Date date3 = new Date();
        date3.setTime(date3.getTime()-5*60*1000);
        if(date1.before(date2)){         //判断起始时间是否早于结束时间
            if(date1.after(date3)){       //判断起始时间是否比date3晚，date3比当前时间早5分钟
                return "时间格式正确";
            }else{
                return "请输入当前时间之后的时间段";
            }
        }else{
            return "起始时间需早于结束时间";
        }
    }

    @Override
    public void deleteById(int id) {
        ordermapper.deleteById(id);
    }

    @Override
    public void update(Order order) {
        ordermapper.update(order);
    }

    @Override
    public void deleteTimeById(int id) {
        ordermapper.deleteTimeById(id);
    }

    @Override
    public int getUserIdByToken(String token) {
        Claims claim=jwtToken.getClaimByToken(token);
        String username= (String) claim.get("username");
        User user=userMapper.loginUser(username);
        return user.getId();
    }

    @Override
    public Order getOrderById(int id) {
        return ordermapper.getOrderById(id);
    }
}
