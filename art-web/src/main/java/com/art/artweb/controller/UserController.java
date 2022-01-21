package com.art.artweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.User;
import com.art.artcommon.utils.Tools;
import com.art.artservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param data
     * @return
     */
    @RequestMapping("/login")
    public IResult login(@RequestBody String data){
        String login = userService.login(data);
        if (login.equals("登录成功")){
            return IResult.success(null);
        }
        return IResult.fail(null,login,"9999");
    }

    /**
     * 用户注册：前端传json字符串，把里面的用户信息存到数据库中，其中一部分非敏感信息生成token并返回
     * @param data
     */
    @RequestMapping("/register")
    public IResult register(@RequestBody String data){
        System.out.println(data);
        JSONObject jsonObject = JSON.parseObject(data);
        jsonObject.remove("confirm_pwd");
        jsonObject.remove("code");
        String date = Tools.date_To_Str((Long) jsonObject.get("timestamp"));
        jsonObject.remove("timestamp");
        jsonObject.put("create_time",date);
        String s = jsonObject.toJSONString();
        User user = JSON.parseObject(s,new TypeReference<User>(){});
        int status = userService.register(user);
        if(status==1){
            String[] args = {"username","status"};
            String token = userService.createToken(s, args);
            JSONObject object = new JSONObject();
            object.put("token",token);
            return IResult.success(object);
        }
        return IResult.fail(null,"注册失败", R.CODE_FAIL);
    }
}
