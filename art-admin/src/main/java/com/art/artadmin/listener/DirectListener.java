package com.art.artadmin.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.art.artcommon.entity.Store;
import com.art.artadmin.entity.User_log;
import com.art.artcommon.utils.RedisUtil;
import com.art.artcommon.utils.SpringContextHolder;
import com.art.artadmin.handler.Handler;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn("SpringContextHolder")
public class DirectListener {

    private CountDownLatch latch = new CountDownLatch(10);
    private LinkedBlockingQueue<User_log> batchQueue = new LinkedBlockingQueue<>(10);
    private Handler handler = SpringContextHolder.getBean("directHandler");

    /**
     * 在 User_log 表中记录用户登录信息
     * 队列消息消费者，方法会按消息顺序进行执行，并当方法执行完成后再执行下一次方法
     * @param data String
     */
    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue(value = "batchSyncTask_user_log",durable = "true",exclusive = "false",autoDelete = "false"))
    public void batchSyncTask(String data){
        User_log log = JSON.parseObject(data,new TypeReference<User_log>(){});
        latch.countDown();
        batchQueue.offer(log);
        if (latch.getCount()==0){//队列已满
            latch = new CountDownLatch(10);
            Store.getInstance().put("batch_deliver",Store.getInstance().MainDataPut("user_log",batchQueue));
            //调用处理方法
            handler.handler("doBatchSync","ready");
            //batchQueue.clear();
        }else {//队列未满
            if (!RedisUtil.hasKey("ifQueueIsFull")){
                latch = new CountDownLatch(10);
                Store.getInstance().put("batch_deliver",Store.getInstance().MainDataPut("user_log",batchQueue));
                handler.handler("doBatchSync","ready");
                //batchQueue.clear();问题出在这里
            }
            RedisUtil.set("ifQueueIsFull","false",60, TimeUnit.SECONDS);
        }
    }
}
