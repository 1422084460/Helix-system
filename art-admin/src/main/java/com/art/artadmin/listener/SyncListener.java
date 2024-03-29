package com.art.artadmin.listener;

import com.art.artcommon.entity.Store;
import com.art.artadmin.entity.User_log;
import com.art.artadmin.mapper.User_logMapper;
import com.art.artcommon.utils.RedisUtil;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class SyncListener {

    @Autowired
    private User_logMapper mapper;

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue(value = "doBatchSync",durable = "true",exclusive = "false",autoDelete = "false"))
    public void insertBatch_User_log(String data){
        if (data.equals("ready")){
            List<User_log> list = new ArrayList<>();
            try {
                LinkedBlockingQueue<User_log> q = (LinkedBlockingQueue<User_log>) Store.Instance().safeGet("batch_deliver", "user_log");
                Store.Instance().remove("batch_deliver");
                q.drainTo(list,q.size());
                mapper.insertBatch(list);
                RedisUtil.set("user_log_queue_sync_finished","true");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
