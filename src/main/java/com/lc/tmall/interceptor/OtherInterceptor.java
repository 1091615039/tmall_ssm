package com.lc.tmall.interceptor;

import com.lc.tmall.pojo.Category;
import com.lc.tmall.pojo.OrderItem;
import com.lc.tmall.pojo.User;
import com.lc.tmall.service.CategoryService;
import com.lc.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class OtherInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        //获取分类集合信息放在搜索栏下
        List<Category> categories = categoryService.list();
        request.getSession().setAttribute("cs", categories);
        //获取当前的contextPath:tmall_ssm,放在左上角那个变形金刚，点击之后才能够跳转到首页，否则点击之后也仅仅停留在当前页面
        request.getSession().setAttribute("contextPath", request.getSession().getServletContext().getContextPath());
        //获取购物车中一共有多少数量
        User user = (User) request.getSession().getAttribute("user");
        int cartTotalItemNumber = 0;
        if (user != null){
            List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
            for (OrderItem orderItem : orderItems){
                cartTotalItemNumber += orderItem.getNumber();
            }
        }
        request.getSession().setAttribute("cartTotalItemNumber", cartTotalItemNumber);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
