package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.art.artcommon.entity.User;
//import com.art.artcommon.mapper.UserMapper;
import com.art.artcommon.utils.JWTUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

//    @Resource
//    private UserMapper userMapper;

    @RequestMapping("/login")
    public String login(@RequestBody User user){
        return null;
    }

    /**
     * 用户注册：前端传json字符串，把里面的用户信息存到数据库中，其中一部分非敏感信息生成token并返回
     * @param data
     */
    @RequestMapping("/register")
    @Transactional
    public void register(@RequestBody String data){
        System.out.println(data);
        Map map = JSON.parseObject(data);
        map.remove("timestamp");
        String token = JWTUtils.getToken(map);
        System.out.println(token);
//        String image = user.getImgUrl();
//        User user1 = new User();
//        user1.setImgUrl(image);
//        UpdateWrapper updateWrapper = new UpdateWrapper();
//        updateWrapper.eq("email","123456@qq.com");
//        updateWrapper.set("imgUrl",image);
//        userMapper.update(null,updateWrapper);
    }
}
