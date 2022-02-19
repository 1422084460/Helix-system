package com.art.artweb;

import com.art.artcommon.entity.Store;
import com.art.artcommon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AppStart implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initStore();
        initRedis();
    }

    @Order(2)
    private void initRedis() {
        if (!RedisUtil.hasKey("isInitAlready")){
            Map<String,String> map = new HashMap<>();
            String email = "getEmail";
            map.put("test",email);
            RedisUtil.pipLine(map,null);
        }
        log.info("redis数据初始化完成......");
    }

    /**
     * 初始化全局变量 Store
     */
    @Order(1)
    private void initStore(){
        Store.getInstance();
        log.info("store初始化完成......");
    }
}
