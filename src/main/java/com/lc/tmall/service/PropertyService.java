package com.lc.tmall.service;

import com.lc.tmall.pojo.Property;

import java.util.List;

public interface PropertyService {
    void delete(int id);
    void add(Property c);
    void update(Property c);
    Property get(int id);
    List list(int cid);
}
