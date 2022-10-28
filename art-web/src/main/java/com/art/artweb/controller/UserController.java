package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artadmin.service.UserPageService;
import com.art.artcommon.constant.R;
import com.art.artcommon.custominterface.AuthL;
import com.art.artcommon.custominterface.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artadmin.entity.User;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artadmin.service.UserAuthService;
import com.art.artweb.async.AsyncTaskMain;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * description
 * 用户管理控制器
 * @author lou
 * @create 2022/9/9
 */
@Api("用户管理接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserPageService userPageService;

    @Autowired
    private AsyncTaskMain task;

    /**
     * 用户登录 (密码或邮箱验证码)
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    @ShowArgs
    public IResult login(@RequestBody JSONObject data){
        String token = "";
        IResult loginStatus = null;
        if (data.get("login_mode").equals(R.CODE_LOGIN_WITH_PWD)){
            loginStatus = userAuthService.login(data);
        }else {
            loginStatus = userAuthService.verifyCode(data);
        }
        if (loginStatus.isSuccess()){
            IResult res = (IResult) Store.getInstance().get(Thread.currentThread().getName()).get("token验证");
            RedisUtil.set("user_auth_" + data.getString("email"),"login",10, TimeUnit.SECONDS);
            if (res.getCode().equals("9101")){
                Object user = loginStatus.getData().get("user");
                String s = JSONArray.toJSON(user).toString();
                String[] args = {"username","status","email"};
                token = userAuthService.createToken(s,args);
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
    @ApiOperation("用户注册")
    @PostMapping("/register")
    @ShowArgs
    public IResult register(@RequestBody JSONObject data){
        String date = Tools.date_To_Str((Long) data.get("timestamp"));
        data.put("create_time",date);
        String s = data.toJSONString();
        User user = JSON.parseObject(s,new TypeReference<User>(){});
        int status = userAuthService.register(user);
        if(status==1){
            String[] args = {"username","status","email"};
            String token = userAuthService.createToken(s, args);
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
    @ApiOperation("密码修改")
    @PostMapping("/changePwd")
    @ShowArgs
    @AuthL
    public IResult changePwd(@RequestBody JSONObject data){
        String email = data.getString("email");
        String newPwd = data.getString("newPassWord");
        int i = userAuthService.changePwd(email, newPwd);
        return i==1 ? IResult.success() : IResult.fail("密码修改失败，请重试",R.CODE_FAIL);
    }

    /**
     * 发送验证码
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("发送验证码")
    @PostMapping("/sendCode")
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
    @ApiOperation("验证验证码")
    @PostMapping("/verifyCode")
    @ShowArgs
    public IResult verifyCode(@RequestBody JSONObject data){
        return userAuthService.verifyCode(data);
    }

    /**
     * 注销当前用户
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("注销用户")
    @PostMapping("/cancelCurrentUser")
    public IResult cancelCurrentUser(@RequestBody JSONObject data){
        return userAuthService.cancelCurrentUser(data);
    }

    /**
     * 用户签到
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("签到")
    @PostMapping("/signIn")
    public IResult signIn(@RequestBody JSONObject data){
        String email = data.getString("email");
        long timestamp = data.getLong("timestamp");
        int score = data.getIntValue("score");
        int signInCount = data.getIntValue("signInCount");
        int signIn = userPageService.signIn(email,timestamp,score,signInCount);
        return signIn==1 ? IResult.success() : IResult.fail("签到失败",R.CODE_FAIL);
    }

    /**
     * 获取用户个人中心页面
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("获取用户个人中心页面")
    @PostMapping("/getUserPageInfo")
    public IResult getUserPageInfo(@RequestBody JSONObject data){
        String email = data.getString("email");
        JSONObject userPage = userPageService.renderUserPage(email);
        return IResult.success(userPage);
    }
}
