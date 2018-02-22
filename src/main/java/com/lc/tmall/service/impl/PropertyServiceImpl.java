package com.lc.tmall.service.impl;

import com.lc.tmall.mapper.PropertyMapper;
import com.lc.tmall.pojo.Property;
import com.lc.tmall.pojo.PropertyExample;
import com.lc.tmall.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PropertyServiceImpl implements PropertyService{
    @Autowired
    PropertyMapper propertyMapper;
    @Override
    public void delete(int id) {
        propertyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void add(Property c) {
        propertyMapper.insert(c);
    }

    @Override
    public void update(Property c) {
        propertyMapper.updateByPrimaryKeySelective(c);
    }

    @Override
    public Property get(int id) {
        return propertyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List list(int cid) {
        PropertyExample example = new PropertyExample();
        example.createCriteria().andCidEqualTo(cid); //找到符合条件的example
        example.setOrderByClause("id desc");
        return propertyMapper.selectByExample(example);
    }
}
