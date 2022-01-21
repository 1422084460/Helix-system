package com.art.artservice.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.entity.User_log;
import com.art.artcommon.mapper.User_logMapper;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = @Queue(value = "user_log",durable = "true",exclusive = "false",autoDelete = "false"))
public class DirectListener {

    @Autowired
    private User_logMapper logMapper;

    /**
     * 在 User_log 表中记录用户登录信息
     * @param data
     */
    @RabbitHandler
    public void add_User_log(String data){
        System.out.println("data = " + data);
        User_log log = JSON.parseObject(data,new TypeReference<User_log>(){});
        logMapper.insert(log);
    }
}
