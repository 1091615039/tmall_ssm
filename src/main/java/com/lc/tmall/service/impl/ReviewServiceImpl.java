package com.lc.tmall.service.impl;

import com.lc.tmall.mapper.ReviewMapper;
import com.lc.tmall.pojo.Review;
import com.lc.tmall.pojo.ReviewExample;
import com.lc.tmall.pojo.User;
import com.lc.tmall.service.ReviewService;
import com.lc.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    ReviewMapper reviewMapper;
    @Autowired
    UserService userService;

    @Override
    public void add(Review review) {
        reviewMapper.insert(review);
    }

    @Override
    public void delete(int id) {
        reviewMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Review review) {
        reviewMapper.updateByPrimaryKeySelective(review);
    }

    @Override
    public Review get(int id) {
        return reviewMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Review> list(int pid) {
        ReviewExample example = new ReviewExample();
        example.createCriteria().andPidEqualTo(pid);
        example.setOrderByClause("id desc");
        List<Review> reviews = reviewMapper.selectByExample(example);
        setUser(reviews);
        return reviews;
    }

    @Override
    public int getCount(int pid) {
        return list(pid).size();
    }

    public void setUser(List<Review> reviews) {
       for (Review review:reviews){
           setUser(review);
       }
    }

    public void setUser(Review review){
        review.setUser(userService.get(review.getUid()));
    }
}
