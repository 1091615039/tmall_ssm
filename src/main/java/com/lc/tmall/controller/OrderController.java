package com.lc.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.tmall.pojo.Order;
import com.lc.tmall.service.OrderItemService;
import com.lc.tmall.service.OrderService;
import com.lc.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @RequestMapping("admin_order_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Order> os = orderService.list();
        int total = (int) new PageInfo<>(os).getTotal();
        page.setTotal(total);
        orderItemService.fill(os);
        model.addAttribute("page", page);
        model.addAttribute("os", os);
        return "admin/listOrder";
    }

    @RequestMapping("admin_order_delivery")
    public String delivery(int id){
        Order order = orderService.get(id);
        order.setStatus(OrderService.waitConfirm);
        order.setDeliveryDate(new Date());
        orderService.update(order);
        return "redirect:admin_order_list";
    }
}
