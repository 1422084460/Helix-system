package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.AuthL;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artadmin.entity.User;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artadmin.service.UserService;
import com.art.artweb.async.AsyncTaskMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncTaskMain task;

    /**
     * 用户登录 (密码或邮箱验证码)
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/login")
    @ShowArgs
    public IResult login(@RequestBody JSONObject data){
        String token = "";
        IResult loginStatus = null;
        if (data.get("login_mode")==R.CODE_LOGIN_WITH_PWD){
            loginStatus = userService.login(data);
        }else {
            loginStatus = userService.verifyCode(data);
        }
        if (loginStatus.isSuccess()){
            IResult res = (IResult) Store.getInstance().get(Thread.currentThread().getName()).get("token验证");
            RedisUtil.set("user_auth_" + data.getString("email"),"login",10, TimeUnit.SECONDS);
            if (res.getCode().equals("9101")){
                Object user = loginStatus.getData().get("user");
                String s = JSONArray.toJSON(user).toString();
                String[] args = {"username","status","email"};
                token = userService.createToken(s,args);
                JSONObject object = new JSONObject();
                object.put("token",token);
                return IResult.success(object);
            }
        }
        return loginStatus;
    }

    /**
     * 用户注册
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/register")
    @ShowArgs
    public IResult register(@RequestBody JSONObject data){
        data.remove("confirm_pwd");
        data.remove("code");
        String date = Tools.date_To_Str((Long) data.get("timestamp"));
        data.remove("timestamp");
        data.put("create_time",date);
        String s = data.toJSONString();
        User user = JSON.parseObject(s,new TypeReference<User>(){});
        int status = userService.register(user);
        if(status==1){
            String[] args = {"username","status","email"};
            String token = userService.createToken(s, args);
            JSONObject object = new JSONObject();
            object.put("token",token);
            return IResult.success(object);
        }else if (status==-1){
            return IResult.fail(R.REGISTER_EMAIL_REPEAT, R.CODE_REGISTER_EMAIL_REPEAT);
        }
        return IResult.fail("注册失败", R.CODE_FAIL);
    }

    /**
     * 进行密码修改
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/changePwd")
    @ShowArgs
    @AuthL
    public IResult changePwd(@RequestBody JSONObject data){
        String email = data.getString("email");
        String newPwd = data.getString("newPassWord");
        int i = userService.changePwd(email, newPwd);
        if (i==1){
            return IResult.success();
        }
        return IResult.fail("密码修改失败，请重试",R.CODE_FAIL);
    }

    /**
     * 发送验证码
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/sendCode")
    @ShowArgs
    public IResult sendCode(@RequestBody JSONObject data){
        String receiver = data.getString("email");
        task.asyncSendCode(receiver);
        return IResult.success();
    }

    /**
     * 验证验证码
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/verifyCode")
    @ShowArgs
    public IResult verifyCode(@RequestBody JSONObject data){
        return userService.verifyCode(data);
    }

    /**
     * 注销当前用户
     * @param data 请求数据
     * @return IResult
     */
    @RequestMapping("/cancelCurrentUser")
    public IResult cancelCurrentUser(@RequestBody JSONObject data){
        return userService.cancelCurrentUser(data);
    }
}
