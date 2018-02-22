package com.lc.tmall.service.impl;

import com.lc.tmall.mapper.OrderMapper;
import com.lc.tmall.pojo.Order;
import com.lc.tmall.pojo.OrderExample;
import com.lc.tmall.pojo.OrderItem;
import com.lc.tmall.pojo.User;
import com.lc.tmall.service.OrderItemService;
import com.lc.tmall.service.OrderService;
import com.lc.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserService userService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public void add(Order c) {
        orderMapper.insert(c);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception") //事务管理
    public float add(Order order, List<OrderItem> orderItems) {
        float total = 0;
        add(order);
        for (OrderItem orderItem : orderItems){
            orderItem.setOid(order.getId());
            orderItemService.update(orderItem);
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
        }
        return total;
    }

    @Override
    public void delete(int id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Order c) {
        orderMapper.updateByPrimaryKeySelective(c);
    }

    @Override
    public Order get(int id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List list() {
        OrderExample example = new OrderExample();
        example.setOrderByClause("id desc");
        List<Order> os = orderMapper.selectByExample(example);
        setUser(os);
        return os;
    }

    @Override
    public List list(int uid, String excludedStatus) {
        OrderExample example =new OrderExample();
        example.createCriteria().andUidEqualTo(uid).andStatusNotEqualTo(excludedStatus);
        example.setOrderByClause("id desc");
        return orderMapper.selectByExample(example);
    }

    public void setUser(Order order){
        User user = userService.get(order.getUid());
        order.setUser(user);
    }

    public void setUser(List<Order> os){
        for (Order o:os){
            setUser(o);
        }
    }
}
