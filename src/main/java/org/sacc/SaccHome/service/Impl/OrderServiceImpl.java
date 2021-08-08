package org.sacc.SaccHome.service.Impl;



import org.sacc.SaccHome.mbg.mapper.OrderMapper;
import org.sacc.SaccHome.mbg.model.Order;
import org.sacc.SaccHome.mbg.model.Page;
import org.sacc.SaccHome.mbg.model.PageParam;
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
}
