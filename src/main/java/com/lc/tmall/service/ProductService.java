package com.lc.tmall.service;

import com.lc.tmall.pojo.Category;
import com.lc.tmall.pojo.Product;

import java.util.List;

public interface ProductService {
    void add(Product product);
    void delete(int id);
    void update(Product product);
    Product get(int id);
    List list(int cid);
    void setFirstProductImage(Product product);
    void fill(Category category);
    void fill(List<Category> categories);
    void fillByRow(List<Category> categories);
    void setSaleAndReviewNumber(Product product);
    void setSaleAndReviewNumber(List<Product> products);

    List<Product> search(String keyword);
}
