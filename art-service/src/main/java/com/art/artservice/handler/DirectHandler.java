package com.art.artservice.handler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectHandler implements Handler{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * rmq 发布消息
     * @param queue
     * @param JsonEntity
     */
    @Override
    public void handler(String queue, String JsonEntity) {
        rabbitTemplate.convertAndSend(queue,JsonEntity);
    }
}
