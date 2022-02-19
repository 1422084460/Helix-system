package com.art.artservice.listener;

import com.art.artcommon.entity.Store;
import com.art.artcommon.entity.User_log;
import com.art.artcommon.mapper.User_logMapper;
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
                LinkedBlockingQueue<User_log> q = (LinkedBlockingQueue<User_log>) Store.getInstance().get("batch_deliver").get("user_log");
                Store.getInstance().get("batch_deliver").remove("user_log");
                Store.getInstance().remove("batch_deliver");
                q.drainTo(list,q.size());
                mapper.insertBatch(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
