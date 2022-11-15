package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art.artadmin.dto.*;
import com.art.artadmin.service.UserPageService;
import com.art.artcommon.constant.R;
import com.art.artcommon.annotations.AuthL;
import com.art.artcommon.annotations.ShowArgs;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.Store;
import com.art.artadmin.entity.User;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.Tools;
import com.art.artadmin.service.UserAuthService;
import com.art.artweb.async.AsyncTaskMain;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    public IResult login(@RequestBody @Valid LoginUserInfo data){
        String token = "";
        IResult loginStatus = null;
        if (data.getLogin_mode().equals(R.CODE_LOGIN_WITH_PWD)){
            loginStatus = userAuthService.login(data.getEmail(),data.getPassword(),data.getTimestamp());
        }else {
            loginStatus = userAuthService.verifyCode(data.getCode(),data.getEmail());
        }
        if (loginStatus.isSuccess()){
            IResult res = (IResult) Store.getInstance().get(Thread.currentThread().getName()).get("token验证");
            RedisUtil.set("user_auth_" + data.getEmail(),"login",10, TimeUnit.SECONDS);
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
    public IResult register(@RequestBody @Valid RegisterUserInfo data){
        String date = Tools.date_To_Str(data.getTimestamp());
        data.setCreate_time(date);
        User user = new User();
        BeanUtils.copyProperties(data,user);
        int status = userAuthService.register(user);
        if(status==1){
            String[] args = {"username","status","email"};
            String token = userAuthService.createToken(JSON.toJSONString(user), args);
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
    public IResult changePwd(@RequestBody @Valid SecretUserInfo data){
        int i = userAuthService.changePwd(data.getEmail(), data.getNewPassWord());
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
    public IResult sendCode(@RequestBody @Valid SecretUserInfo data){
        task.asyncSendCode(data.getEmail());
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
    public IResult verifyCode(@RequestBody @Valid CodeValidation data){
        return userAuthService.verifyCode(data.getCode(),data.getEmail());
    }

    /**
     * 注销当前用户
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("注销用户")
    @PostMapping("/cancelCurrentUser")
    public IResult cancelCurrentUser(@RequestBody @Valid SecretUserInfo data){
        return userAuthService.cancelCurrentUser(data.getEmail(),data.getTimestamp());
    }

    /**
     * 用户签到
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("签到")
    @PostMapping("/signIn")
    public IResult signIn(@RequestBody @Valid SignInUserInfo data){
        int signIn = userPageService.signIn(data.getEmail(),data.getTimestamp(),data.getScore(),data.getSignInCount());
        return signIn==1 ? IResult.success() : IResult.fail("签到失败",R.CODE_FAIL);
    }

    /**
     * 获取用户个人中心页面
     * @param data 请求数据
     * @return IResult
     */
    @ApiOperation("获取用户个人中心页面")
    @PostMapping("/getUserPageInfo")
    public IResult getUserPageInfo(@RequestBody @Valid BaseUserInfo data){
        JSONObject userPage = userPageService.renderUserPage(data.getEmail());
        return IResult.success(userPage);
    }
}
