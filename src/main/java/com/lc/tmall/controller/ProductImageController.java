package com.lc.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.lc.tmall.pojo.Product;
import com.lc.tmall.pojo.ProductImage;
import com.lc.tmall.service.ProductImageService;
import com.lc.tmall.service.ProductService;
import com.lc.tmall.util.ImageUtil;
import com.lc.tmall.util.Page;
import com.lc.tmall.util.UploadImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("")
public class ProductImageController {
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;

    @RequestMapping("admin_productImage_list")
    public String list(Model model, int pid){
        Product p = productService.get(pid);
        List<ProductImage> pisSingle = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> pisDetail = productImageService.list(pid, ProductImageService.type_detail);
        model.addAttribute("p", p);
        model.addAttribute("pisSingle", pisSingle);
        model.addAttribute("pisDetail", pisDetail);
        return "admin/listProductImage";
    }

    @RequestMapping("admin_productImage_add")
    public String add(ProductImage productImage, HttpSession session, UploadImageFile uploadImageFile){
        productImageService.add(productImage);
        String fileName = productImage.getId() + ".jpg";
        String imageFolder;
        String imageFolder_middle = null;
        String imageFolder_small = null;
        if (productImage.getType().equals(ProductImageService.type_single)){
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");
        }else {
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
        }
        File file = new File(imageFolder, fileName);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            uploadImageFile.getImage().transferTo(file);
            BufferedImage image = ImageUtil.change2jpg(file);
            ImageIO.write(image, "jpg", file);
            if (productImage.getType().equals(ProductImageService.type_single)){
                File f_small = new File(imageFolder_small, fileName);
                File f_middle = new File(imageFolder_middle, fileName);
                ImageUtil.resizeImage(file, 56, 56, f_small);
                ImageUtil.resizeImage(file, 217, 190, f_middle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }

    @RequestMapping("admin_productImage_delete")
    public String delete(int id, HttpSession session){
        ProductImage productImage = productImageService.get(id);
        String fileName = productImage.getId() + ".jpg";
        String imageFolder;
        String imageFolder_middle;
        String imageFolder_small;
        if (productImage.getType().equals(ProductImageService.type_single)){
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");
            File file = new File(imageFolder, fileName);
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            file.delete();
            f_small.delete();
            f_middle.delete();
        }else {
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
            File file = new File(imageFolder, fileName);
            file.delete();
        }
        productImageService.delete(id);
        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }
}
