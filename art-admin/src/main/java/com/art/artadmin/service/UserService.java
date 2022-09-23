package com.art.artadmin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artadmin.entity.User_cancel;
import com.art.artadmin.mapper.User_cancelMapper;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private User_cancelMapper cancelMapper;

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

    /**
     * 注销当前用户
     * @param data 请求数据
     * @return IResult
     */
    @Transactional
    public IResult cancelCurrentUser(JSONObject data){
        String email = data.getString("email");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        User one = userMapper.selectOne(wrapper);
        String userJson = JSON.toJSONString(one);
        User_cancel cancel = JSON.parseObject(userJson,new TypeReference<User_cancel>(){});
        cancel.setStatus("cancel");
        String cancel_time = Tools.date_To_Str(data.getLong("timestamp"));
        cancel.setCancel_time(cancel_time);
        int insert = cancelMapper.insert(cancel);
        int delete = userMapper.delete(wrapper);
        if (insert==1 && delete==1){
            return IResult.success();
        }
        return IResult.fail("因未知错误注销失败，请稍后重试",R.CODE_FAIL);
    }

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
