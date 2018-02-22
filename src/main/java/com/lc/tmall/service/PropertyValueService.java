package com.lc.tmall.service;

import com.lc.tmall.pojo.Product;
import com.lc.tmall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {
    void init(Product product);
    void update(PropertyValue propertyValue);
    PropertyValue get(int ptid, int pid);
    List<PropertyValue> list(int pid);
}
