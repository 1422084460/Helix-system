package com.art.artadmin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art.artcommon.constant.R;
import com.art.artcommon.entity.IResult;
import com.art.artcommon.entity.User;
import com.art.artcommon.entity.User_log;
import com.art.artcommon.mapper.UserMapper;
import com.art.artcommon.utils.JWTUtils;
import com.art.artcommon.utils.SpringContextHolder;
import com.art.artcommon.utils.Tools;
import com.art.artadmin.handler.Handler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private Handler handler;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<User> QueryUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name","a");
        List<User> users = userMapper.selectList(wrapper);
        stopWatch.stop();
        return users;
    }

    public void QueryUserTemplate(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.like("name","a");
        wrapper.set("name","abc");
        //transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);//设置事务传播行为，默认为REQUIRED
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                userMapper.update(null,wrapper);
                //int i=1/0;
            }
        });
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Transactional
    public int register(User user){
        int status = 0;
        try {
            status = userMapper.insert(user);
        }catch (Exception e){
            System.out.println("status:"+status);
        }
        return status;
    }

    /**
     * 创建 token
     * @param data
     * @param payload_args
     * @return
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
     * @param data
     * @return
     */
    public IResult login(String data){
        Map map = JSON.parseObject(data);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",map.get("email")).eq("password",map.get("password"));
        User user = userMapper.selectOne(wrapper);
        if (user!=null){
            handler = SpringContextHolder.getBean("directHandler");
            String date = Tools.date_To_Str((long) map.get("timestamp"));
            User_log userLog = new User_log().setUsername(user.getUsername()).setEmail(user.getEmail()).setLogin_time(date).setEvent(R.USER_LOGIN);
            handler.handler("batchSyncTask_user_log",JSONObject.toJSONString(userLog));
            JSONObject object = new JSONObject();
            user.setPassword("");
            object.put("user",user);
            return IResult.success(object);
        }
        return IResult.fail(null,"账号或密码错误!",R.CODE_FAIL);
    }
}
