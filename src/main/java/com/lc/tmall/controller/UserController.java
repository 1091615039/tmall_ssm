package com.lc.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.tmall.pojo.User;
import com.lc.tmall.service.UserService;
import com.lc.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("admin_user_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<User> users = userService.list();
        int total = (int) new PageInfo<>(users).getTotal();
        page.setTotal(total);
        model.addAttribute("page", page);
        model.addAttribute("us", users);
        return "admin/listUser";
    }
}
