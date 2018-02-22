package com.lc.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.lc.tmall.comparator.*;
import com.lc.tmall.pojo.*;
import com.lc.tmall.service.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class ForeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @RequestMapping("forehome")
    public String home(Model model){
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        model.addAttribute("cs", cs);
        return "fore/home";
    }

    @RequestMapping("foreregister")
    public String register(Model model, User user){
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);//对name中的特殊字符进行转义，以解决恶意注册等问题
        user.setName(name);
        boolean exist = userService.isExist(name);
        if (exist){
            String msg = "用户名已存在！";
            model.addAttribute("user", null);
            model.addAttribute("msg", msg);
            return "fore/register";
        }
        userService.add(user);
        return "redirect:registerSuccessPage";
    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, Model model, HttpSession session){
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user == null){
            model.addAttribute("msg", "用户名或密码错误");
            return "fore/login";
        }
        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("foreproduct")
    public String product(int pid, Model model){
        Product product = productService.get(pid);
        List<ProductImage> productSingleImages = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(pid, ProductImageService.type_detail);
        product.setProductDetailImages(productDetailImages);
        product.setProductSingleImages(productSingleImages);
        List<PropertyValue> pvs = propertyValueService.list(pid);
        List<Review> reviews = reviewService.list(pid);
        productService.setSaleAndReviewNumber(product);
        model.addAttribute("p", product);
        model.addAttribute("pvs", pvs);
        model.addAttribute("reviews", reviews);
        return "fore/product";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin( HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null!=user)
            return "success";
        return "fail";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session){
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user == null){
            return "fail";
        }
        session.setAttribute("user", user);
        return "success";
    }

    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        if (sort != null){
            switch (sort){
                case "review":
                    Collections.sort(category.getProducts(), new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        model.addAttribute("c", category);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String search(String keyword, Model model){
        PageHelper.offsetPage(0, 20);
        List<Product> products = productService.search(keyword);
        productService.setSaleAndReviewNumber(products);
        model.addAttribute("ps", products);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone") //立即购买
    public String buyone(int pid, int num, HttpSession session){
        Product p = productService.get(pid);
        int oiid = 0;
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
        for (OrderItem orderItem : orderItems){
            if (orderItem.getProduct().getId().intValue() == p.getId().intValue()){
                orderItem.setNumber(orderItem.getNumber() + num);
                orderItemService.update(orderItem);
                found = true;
                oiid = orderItem.getId();
                break;
            }
        }
        if (!found){
            OrderItem orderItem = new OrderItem();
            orderItem.setPid(pid);
            orderItem.setUid(user.getId());
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }
        return "redirect:forebuy?oiid=" + oiid;
    }

    @RequestMapping("forebuy") //结算页面
    public String buy(Model model, String[] oiid, HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (String strid : oiid){
            int id = Integer.parseInt(strid);
            OrderItem orderItem = orderItemService.get(id);
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
            orderItems.add(orderItem);
        }
        session.setAttribute("ois", orderItems);
        //model.addAttribute("ois", orderItems);
        model.addAttribute("total", total);
        return "fore/buy";
    }

    @RequestMapping("foreaddCart") //添加到购物车
    @ResponseBody
    public String addCart(int pid, int num, HttpSession session){
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProduct().getId().intValue() == p.getId().intValue()) {
                orderItem.setNumber(orderItem.getNumber() + num);
                orderItemService.update(orderItem);
                found = true;
                break;
            }
        }
        if (!found){
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(num);
            orderItem.setPid(pid);
            orderItem.setUid(user.getId());
            orderItemService.add(orderItem);
        }
        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", orderItems);
        return "fore/cart";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(int pid, int num, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "fail";
        List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
        for (OrderItem orderItem : orderItems){
            if (orderItem.getProduct().getId().intValue() == pid){
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                break;
            }
        }
        return "success";
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(int oiid, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "fail";
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder(HttpSession session, Order order){
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setStatus(OrderService.waitPay);
        order.setUser(user);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(order, orderItems);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;
    }

    @RequestMapping("forepayed") //付款成功页
    public String payed(int oid, Model model) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed";
    }

    @RequestMapping("forebought") //订单页
    public String bought(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.list(user.getId(), OrderService.delete);
        orderItemService.fill(orders);
        model.addAttribute("os", orders);
        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay") //确认支付页
    public String confirmPay(int oid, Model model){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        model.addAttribute("o", order);
        return "fore/confirmPay";
    }

    @RequestMapping("foreorderConfirmed") //成功付款页
    public String orderConfirmed(int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return "success";
    }

    @RequestMapping("forereview") //评价页
    public String review(int oid, Model model){
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p.getId());
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("o", o);
        model.addAttribute("p", p);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    @RequestMapping("foredoreview") //评价页点击“提交评价”
    public String doreview(HttpSession session, @RequestParam("oid") int oid, @RequestParam("pid") int pid, String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);
        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);
        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);
        return "redirect:forereview?oid="+oid+"&showonly=true";
    }
}
