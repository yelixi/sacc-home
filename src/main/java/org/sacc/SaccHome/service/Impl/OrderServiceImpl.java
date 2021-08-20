package org.sacc.SaccHome.service.Impl;



import io.jsonwebtoken.Claims;
import org.sacc.SaccHome.api.CommonResult;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    public Page<Order> findNextWeek(int currentPage) {
        PageParam pageParam = new PageParam();
        Page<Order> page = new Page<Order>();
        pageParam.setCurrentPage(currentPage);
        page.setList(ordermapper.findNextWeek(pageParam));
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
        if(date1.before(date2)){         //判断起始时间是否早于结束时间
            if(date1.after(new Date())){       //判断起始时间是否比当前时间晚
                return "时间格式正确";
            }else{
                return "请输入今日之后的时间段";
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
        List<User> users=userMapper.selectUserByUserName(username);
        int id = 0;
        for(User user:users){
            id=user.getId();
        }
        return id;
    }

    @Override
    public Order getOrderById(int id) {
        return ordermapper.getOrderById(id);
    }
}
