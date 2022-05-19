package com.art.artsearch.service;

import com.art.artadmin.entity.User;
import com.art.artadmin.mapper.UserMapper;
import com.art.artsearch.dao.ProductDao;
import com.art.artsearch.dao.UserDao;
import com.art.artsearch.document.Product;
import com.art.artsearch.document.Use;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author lou
 * @create 2022/5/12
 */
@Service
public class EsService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ElasticsearchRestTemplate template;

    public void save(){
        Product p = new Product();
        p.setId(1);
        p.setName("abc");
        p.setCategory("qqq");
        productDao.save(p);
    }

    public void find(){
        productDao.findAll().forEach(System.out::println);
        userDao.findAll().forEach(System.out::println);
    }

    public void del(){
        productDao.deleteById(1);
    }

    public void importData(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.isNotNull("username");
        List<User> users = userMapper.selectList(wrapper);
        List<Use> uses = new ArrayList<>();
        for (User user : users){
            Use use = new Use()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword())
                    .setAvatar(user.getAvatar())
                    .setIs_avatar_prepare(user.getIs_avatar_prepare())
                    .setStatus(user.getStatus())
                    .setIs_admin(user.getIs_admin())
                    .setScore(user.getScore())
                    .setMoney(user.getMoney())
                    .setPhone_num(user.getPhone_num())
                    .setCreate_time(user.getCreate_time());
            uses.add(use);
        }
        userDao.saveAll(uses);
    }
}
