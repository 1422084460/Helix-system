package com.art.artadmin.service;

import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.entity.User;
import com.art.artadmin.mapper.UserMapper;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * description
 * 用户详情页服务类
 * @author lou
 * @create 2022/9/26
 */
@Service
public class UserPageService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户签到
     * @param email 邮箱
     * @return int
     */
    @Transactional
    public int signIn(String email,long timestamp,int score,int signInCount){
        int update = 0;
        long timeoutToday = Tools.computeTimeToMN(0);
        long timeoutTomorrow = Tools.computeTimeToMN(1);
        if (RedisUtil.hasKey("ifSignInAlready-"+email)){
            return update;
        }
        boolean flag = Tools.ifSignInContinue(email);
        try {
            RedisUtil.set("ifSignInAlready-"+email,"true",timeoutToday,TimeUnit.SECONDS);
            RedisUtil.set("ifSignInContinue-"+email,"true",timeoutTomorrow,TimeUnit.SECONDS);
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.set("sign_in_status",1)
                    .set("last_sign_in",timestamp)
                    .set(flag,"score",score+Tools.computeScore(signInCount))
                    .set(!flag,"score",score+Tools.computeScore(0))
                    .set(flag,"sign_in_count",signInCount+1)
                    .set(!flag,"sign_in_count",1)
                    .eq("email",email);
            update = userMapper.update(null, wrapper);
        }catch (Exception e){
            return 0;
        }
        return update;
    }

    /**
     * 获取用户个人中心用户信息
     * @param email 邮箱
     * @return JSONObject
     */
    public JSONObject getUserPageUserInfo(String email){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        User user = userMapper.selectOne(wrapper);
        JSONObject result = new JSONObject();
        result.put("email",user.getEmail());
        result.put("username",user.getUsername());
        result.put("avatar",user.getAvatar());
        result.put("score",user.getScore());
        result.put("signInStatus",user.getSign_in_status());
        result.put("signInCount",user.getSign_in_count());
        result.put("roleKey",user.getRole_key());
        return result;
    }

    //渲染整个用户个人中心界面
    public JSONObject renderUserPage(String email){
        getUserPageUserInfo(email);
        return null;
    }
}
