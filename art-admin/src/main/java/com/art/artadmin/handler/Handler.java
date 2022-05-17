package com.art.artadmin.handler;

/**
 * description
 * 消息队列统一接口
 * @author lou
 * @create 2022/4/11
 */
public interface Handler {

    /**
     * 消息处理
     * @param queue 队列名
     * @param JsonEntity 消息
     */
    void handler(String queue, String JsonEntity);
}
