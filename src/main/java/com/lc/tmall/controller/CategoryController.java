package com.lc.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.tmall.pojo.Category;
import com.lc.tmall.service.CategoryService;
import com.lc.tmall.util.ImageUtil;
import com.lc.tmall.util.Page;
import com.lc.tmall.util.UploadImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping("admin_category_list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(), page.getCount()); //通过分页插件指定分页参数
        List<Category> cs = categoryService.list();
        int total = (int) new PageInfo<>(cs).getTotal(); //通过PageInfo获取总数
        page.setTotal(total);
        model.addAttribute("page", page);
        model.addAttribute("cs", cs);
        return "admin/listCategory";
    }

    @RequestMapping("admin_category_add")
    public String add(Category c, HttpSession session, UploadImageFile uploadImageFile) throws IOException {  //参数 Category c接受页面提交的分类名称,参数 session 用于在后续获取当前应用的路径,UploadedImageFile 用于接受上传的图片
        categoryService.add(c);
        File imageFolder = new File(session.getServletContext().getRealPath("img/category")); //定位存放分类图片的路径
        File file = new File(imageFolder, c.getId() + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        uploadImageFile.getImage().transferTo(file);//把浏览器传递过来的图片保存在上述指定的位置
        BufferedImage img = ImageUtil.change2jpg(file);//确保图片格式一定是jpg，而不仅仅是后缀名是jpg
        ImageIO.write(img, "jpg", file);//把图片格式转换成jpg
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_delete")
    public String delete(int id, HttpSession session){
        categoryService.delete(id);
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_edit")
    public String edit(int id, Model model){
        Category c = categoryService.get(id);
        model.addAttribute("c", c);
        return "admin/editCategory";
    }

    @RequestMapping("admin_category_update")
    public String update(Category category, HttpSession session, UploadImageFile uploadImageFile) throws IOException {
        categoryService.update(category);
        MultipartFile image = uploadImageFile.getImage();
        if (image != null && !image.isEmpty()){
            File imageFolder = new File(session.getServletContext().getRealPath("img/category")); //定位存放分类图片的路径
            File file = new File(imageFolder, category.getId() + ".jpg");
            image.transferTo(file);
            ImageIO.write(ImageUtil.change2jpg(file), "jpg", file);
        }
        return "redirect:/admin_category_list";
    }
}
