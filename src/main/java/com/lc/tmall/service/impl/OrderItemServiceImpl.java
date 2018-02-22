package com.lc.tmall.service.impl;

import com.lc.tmall.mapper.OrderItemMapper;
import com.lc.tmall.pojo.Order;
import com.lc.tmall.pojo.OrderItem;
import com.lc.tmall.pojo.OrderItemExample;
import com.lc.tmall.pojo.Product;
import com.lc.tmall.service.OrderItemService;
import com.lc.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService{
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ProductService productService;

    @Override
    public void add(OrderItem c) {
        orderItemMapper.insert(c);
    }

    @Override
    public void delete(int id) {
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderItem c) {
        orderItemMapper.updateByPrimaryKeySelective(c);
    }

    @Override
    public OrderItem get(int id) {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(id);
        setProduct(orderItem);
        return orderItem;
    }

    @Override
    public List list() {
        OrderItemExample example = new OrderItemExample();
        example.setOrderByClause("id desc");
        return orderItemMapper.selectByExample(example);
    }

    @Override
    public void fill(List<Order> os) {
        for (Order o : os) {
            fill(o);
        }
    }

    @Override
    public void fill(Order o) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOidEqualTo(o.getId());
        example.setOrderByClause("id desc");
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        setProduct(ois);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi:ois){
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
        }
        o.setTotal(total);
        o.setTotalNumber(totalNumber);
        o.setOrderItems(ois);
    }

    @Override
    public int getSaleCount(int pid) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andPidEqualTo(pid);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        int total = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getNumber();
        }
        return total;
    }

    @Override
    public List<OrderItem> listByUser(int uid) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andUidEqualTo(uid).andOidIsNull();
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        setProduct(orderItems);
        return orderItems;
    }

    public void setProduct(OrderItem orderItem){
        Product p = productService.get(orderItem.getPid());
        orderItem.setProduct(p);
    }

    public void setProduct(List<OrderItem> list){
        for (OrderItem orderItem:list){
            setProduct(orderItem);
        }
    }
}
