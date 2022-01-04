package com.art.artservice.service;

import com.art.artcommon.entity.User;
import com.art.artcommon.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@MapperScan("com.art.artcommon.mapper")
@ComponentScan(basePackages = {"com.art.artcommon"})
public class TestService {

    @Autowired
    private UserMapper userMapper;

    public User getUser(){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("password","123456");
        return userMapper.selectOne(wrapper);
//        User u = new User();
//        u.setEmail(m.aaa());
//        return u;
    }
}
