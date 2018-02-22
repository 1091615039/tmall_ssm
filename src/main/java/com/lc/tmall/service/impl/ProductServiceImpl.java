package com.lc.tmall.service.impl;

import com.lc.tmall.mapper.ProductMapper;
import com.lc.tmall.pojo.*;
import com.lc.tmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public void add(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void delete(int id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public Product get(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        setCategory(product);
        setFirstProductImage(product);
        return product;
    }

    @Override
    public List<Product> list(int cid) {
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        List<Product> ps = productMapper.selectByExample(example);
        setCategory(ps);
        setFirstProductImage(ps);
        return ps;
    }

    @Override
    public void setFirstProductImage(Product product) {
        List<ProductImage> pis = productImageService.list(product.getId(), ProductImageService.type_single);
        if (!pis.isEmpty()){
            product.setFirstProductImage(pis.get(0));
        }
    }

    @Override
    public void fill(Category category) {
        List<Product> products = list(category.getId());
        category.setProducts(products);
    }

    @Override
    public void fill(List<Category> categories) {
        for (Category category:categories){
            fill(category);
        }
    }

    @Override
    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for (Category category:categories){
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i<products.size(); i += productNumberEachRow){
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size():size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Override
    public void setSaleAndReviewNumber(Product product) {
        product.setSaleCount(orderItemService.getSaleCount(product.getId()));
        product.setReviewCount(reviewService.getCount(product.getId()));
    }

    @Override
    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products){
            setFirstProductImage(product);
        }
    }

    @Override
    public List<Product> search(String keyword) {
        ProductExample example = new ProductExample();
        example.createCriteria().andNameLike("%" + keyword + "%");
        example.setOrderByClause("id desc");
        List<Product> products = productMapper.selectByExample(example);
        setFirstProductImage(products);
        setCategory(products);
        return products;
    }

    public void setFirstProductImage(List<Product> ps){
        for (Product p:ps){
            setFirstProductImage(p);
        }
    }

    public void setCategory(Product product){
        Category category = categoryService.get(product.getCid());
        product.setCategory(category);
    }

    public void setCategory(List<Product> ps){
        for (Product p:ps) {
            setCategory(p);
        }
    }
}
