package com.art.artadmin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artadmin.entity.User;
import com.art.artadmin.entity.User_log;
import com.art.artadmin.mapper.UserMapper;
import com.art.artcommon.utils.JWTUtils;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.SpringContextHolder;
import com.art.artcommon.utils.Tools;
import com.art.artadmin.handler.Handler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private Handler handler;

    /**
     * 用户注册
     * @param user 用户
     * @return int
     */
    @Transactional
    public int register(User user){
        int status = 0;
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",user.getEmail());
        User one = userMapper.selectOne(wrapper);
        if (one==null){
            try {
                user.setPassword(Tools.toMd5(user.getPassword()));
                status = userMapper.insert(user);
            }catch (Exception e){
                System.out.println("status:"+status);
            }
        }else {
            status = -1;
        }
        return status;
    }

    /**
     * 创建 token
     * @param data 请求数据
     * @param payload_args 负载
     * @return String
     */
    public String createToken(String data,String[] payload_args){
        Map map = JSON.parseObject(data);
        Map payload = new HashMap();
        for (String s : payload_args){
            payload.put(s,map.get(s));
        }
        return JWTUtils.getToken(payload);
    }

    /**
     * 用户登录
     * @param data 请求数据
     * @return IResult
     */
    public IResult login(JSONObject data){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",data.getString("email"))
                .eq("password",Tools.toMd5(data.getString("password")));
        User user = userMapper.selectOne(wrapper);
        if (user!=null){
            handler = SpringContextHolder.getBean("directHandler");
            String date = Tools.date_To_Str(data.getLong("timestamp"));
            User_log userLog = new User_log()
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setLogin_time(date)
                    .setEvent(R.USER_LOGIN);
            handler.handler("batchSyncTask_user_log",JSONObject.toJSONString(userLog));
            JSONObject object = new JSONObject();
            user.setPassword("");
            object.put("user",user);
            return IResult.success(object);
        }
        return IResult.fail("账号或密码错误!",R.CODE_FAIL);
    }

    /**
     * 修改密码
     * @param email 邮箱
     * @param newPassWord 新密码
     * @return int
     */
    @Transactional
    public int changePwd(String email,String newPassWord){
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("password",newPassWord).eq("email",email);
        return userMapper.update(null, wrapper);
    }

    /**
     * 验证验证码
     * @param data 请求数据
     * @return IResult
     */
    public IResult verifyCode(JSONObject data){
        String code = data.getString("code");
        String email = data.getString("email");
        if (RedisUtil.hasHashKey(email,"verifyCode")){
            if (RedisUtil.getHash(email,"verifyCode").equals(code)){
                return IResult.success();
            }
            return IResult.fail("验证码错误",R.CODE_VERIFY_FAIL);
        }
        return IResult.fail("验证码已失效，请重新获取！",R.CODE_VERIFY_EXPIRE);
    }
}
