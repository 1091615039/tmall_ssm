package com.lc.tmall.service;

import com.lc.tmall.pojo.Category;
import com.lc.tmall.util.Page;

import java.util.List;

public interface CategoryService {
    List<Category> list();
    void add(Category category);
    void delete(int id);
    Category get(int id);
    void update(Category category);
}
